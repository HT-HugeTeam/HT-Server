import { Injectable } from '@nestjs/common';
import OpenAI from 'openai';
import {
  SYSTEM_IMAGE_ANALYSIS,
  SYSTEM_VIDEO_SCRIPT,
  userImageAnalysisPrompt,
} from './constant/prompts';
import {
  ImageAnalysis,
  VideoScript,
  VideoScriptTemplate3,
  VoiceoverResult,
  DynamicTiming,
  DynamicTimingTemplate3,
  TextSegment,
  CreatomateModifications,
} from './types';
import { parseBuffer } from 'music-metadata';
import {
  GetObjectCommand,
  PutObjectCommand,
  S3Client,
} from '@aws-sdk/client-s3';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';

import { randomUUID } from 'crypto';

@Injectable()
export class AppService {
  private readonly s3Client = new S3Client({
    region: 'ap-northeast-2',
    credentials: {
      accessKeyId: process.env.AWS_ACCESS_KEY_ID!,
      secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY!,
    },
  });

  private readonly openAiClient = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
  });

  private readonly CMUrl = 'https://api.creatomate.com/v1/renders';
  private readonly CMApiKey = process.env.CM_API_KEY!;

  async analyzeImage(
    imageUrls: string[],
    text: string,
  ): Promise<ImageAnalysis> {
    const response = await this.openAiClient.responses.create({
      model: 'gpt-5-nano-2025-08-07',
      reasoning: { effort: 'minimal' },
      input: [
        {
          role: 'system',
          content: SYSTEM_IMAGE_ANALYSIS,
        },
        {
          role: 'user',
          content: [
            ...imageUrls.map((imageUrl) => ({
              type: 'input_image' as const,
              image_url: imageUrl,
              detail: 'auto' as const,
            })),
            {
              type: 'input_text' as const,
              text: userImageAnalysisPrompt(text, imageUrls),
            },
          ] as const,
        },
      ],
      max_output_tokens: 400,
    });

    console.log(JSON.parse(response.output_text));

    return JSON.parse(response.output_text) as ImageAnalysis;
  }

  async createVideoScript(
    imageAnalysis: ImageAnalysis,
    text: string,
  ): Promise<VideoScript> {
    const response = await this.openAiClient.responses.create({
      model: 'gpt-5-nano-2025-08-07',
      reasoning: { effort: 'minimal' },
      input: [
        {
          role: 'system',
          content: SYSTEM_VIDEO_SCRIPT,
        },
        {
          role: 'user',
          content: [
            ...imageAnalysis.imageDescriptions.map((description) => {
              return {
                type: 'input_text' as const,
                text: `description:${description.description},caption:${description.caption}`,
              };
            }),
            {
              type: 'input_text' as const,
              text,
            },
          ] as const,
        },
      ],
      max_output_tokens: 1000,
    });

    console.log(JSON.parse(response.output_text));

    return JSON.parse(response.output_text) as VideoScript;
  }

  async createVideoScriptTemplate3(
    imageAnalysis: ImageAnalysis,
    text: string,
  ): Promise<VideoScriptTemplate3> {
    const response = await this.openAiClient.responses.create({
      model: 'gpt-5-nano-2025-08-07',
      reasoning: { effort: 'minimal' },
      input: [
        {
          role: 'system',
          content: `${SYSTEM_VIDEO_SCRIPT}

IMPORTANT: Generate exactly 3 body sections for Template 3:
- body[0]: First menu item description
- body[1]: Second menu item description
- body[2]: Restaurant atmosphere/ambiance description

The output must follow this exact structure with 3 body sections.`,
        },
        {
          role: 'user',
          content: [
            ...imageAnalysis.imageDescriptions.map((description) => {
              return {
                type: 'input_text' as const,
                text: `description:${description.description},caption:${description.caption}`,
              };
            }),
            {
              type: 'input_text' as const,
              text,
            },
          ] as const,
        },
      ],
      max_output_tokens: 1200,
    });

    console.log('Template 3 VideoScript:', JSON.parse(response.output_text));

    return JSON.parse(response.output_text) as VideoScriptTemplate3;
  }

  async createVoiceoverWithDuration(
    videoScript: VideoScript | VideoScriptTemplate3,
  ): Promise<VoiceoverResult> {
    const hookVoiceover = await this.openAiClient.audio.speech.create({
      model: 'gpt-4o-mini-tts',
      input: videoScript.hook.voiceover,
      voice: 'coral',
      instructions: 'Speak in a cheerful and positive tone.',
    });
    const hookBuffer = Buffer.from(await hookVoiceover.arrayBuffer());
    const hookDuration = await this.extractAudioDuration(hookBuffer);
    const hookVoiceoverUrl = await this.uploadVoiceover(
      hookBuffer,
      `${randomUUID()}.mp3`,
    );

    const bodyVoiceoverList = await Promise.all(
      videoScript.body.map((b) =>
        this.openAiClient.audio.speech.create({
          model: 'gpt-4o-mini-tts',
          input: b.voiceover,
          voice: 'coral',
          instructions: 'Speak in a cheerful and positive tone.',
        }),
      ),
    );

    const bodyBuffers = await Promise.all(
      bodyVoiceoverList.map(async (voiceover) =>
        Buffer.from(await voiceover.arrayBuffer()),
      ),
    );

    const bodyAudioWithDuration = await Promise.all(
      bodyBuffers.map(async (buffer) => {
        const duration = await this.extractAudioDuration(buffer);
        const url = await this.uploadVoiceover(buffer, `${randomUUID()}.mp3`);
        return { url, duration };
      }),
    );

    const ctaVoiceover = await this.openAiClient.audio.speech.create({
      model: 'gpt-4o-mini-tts',
      input: videoScript.cta.voiceover,
      voice: 'coral',
      instructions: 'Speak in a cheerful and positive tone.',
    });
    const ctaBuffer = Buffer.from(await ctaVoiceover.arrayBuffer());
    const ctaDuration = await this.extractAudioDuration(ctaBuffer);
    const ctaVoiceoverUrl = await this.uploadVoiceover(
      ctaBuffer,
      `${randomUUID()}.mp3`,
    );

    const result: VoiceoverResult = {
      hook: { url: hookVoiceoverUrl, duration: hookDuration },
      body: bodyAudioWithDuration,
      cta: { url: ctaVoiceoverUrl, duration: ctaDuration },
    };

    console.log(result);

    return result;
  }

  private async extractAudioDuration(buffer: Buffer): Promise<number> {
    const metadata = await parseBuffer(buffer);
    return metadata.format.duration || 0;
  }

  private segmentTextForSection(
    text: string,
    elementIds: string[],
    sectionStartTime: number,
    sectionDuration: number,
  ): TextSegment[] {
    if (!text || !elementIds.length || sectionDuration <= 0) {
      return elementIds.map((elementId) => ({
        elementId,
        text: text || '',
        time: sectionStartTime,
        duration: sectionDuration,
      }));
    }

    const words = text.split(' ').filter((word) => word.length > 0);
    const wordsPerSegment = Math.ceil(words.length / elementIds.length);
    const segmentDuration = sectionDuration / elementIds.length;

    return elementIds.map((elementId, index) => {
      const start = index * wordsPerSegment;
      const end = Math.min(start + wordsPerSegment, words.length);
      const segmentText = words.slice(start, end).join(' ');

      return {
        elementId,
        text: segmentText || text,
        time: sectionStartTime + index * segmentDuration,
        duration: segmentDuration,
      };
    });
  }

  private calculateDynamicTiming(
    voiceoverResult: VoiceoverResult,
  ): DynamicTiming {
    const hookStartTime = 0;
    const hookDuration = voiceoverResult.hook.duration;

    const bodyAStartTime = hookStartTime + hookDuration;
    const bodyADuration = voiceoverResult.body[0].duration;

    const bodyBStartTime = bodyAStartTime + bodyADuration;
    const bodyBDuration = voiceoverResult.body[1].duration;

    const ctaStartTime = bodyBStartTime + bodyBDuration;
    const ctaDuration = voiceoverResult.cta.duration;

    const totalDuration =
      hookDuration + bodyADuration + bodyBDuration + ctaDuration;

    return {
      hookStartTime,
      hookDuration,
      bodyAStartTime,
      bodyADuration,
      bodyBStartTime,
      bodyBDuration,
      ctaStartTime,
      ctaDuration,
      totalDuration,
    };
  }

  private calculateDynamicTimingTemplate3(
    voiceoverResult: VoiceoverResult,
  ): DynamicTimingTemplate3 {
    if (voiceoverResult.body.length < 3) {
      throw new Error('VideoScript must have 3 body sections for Template 3');
    }

    const hookStartTime = 0;
    const hookDuration = voiceoverResult.hook.duration;

    const bodyAStartTime = hookStartTime + hookDuration;
    const bodyADuration = voiceoverResult.body[0].duration;

    const bodyBStartTime = bodyAStartTime + bodyADuration;
    const bodyBDuration = voiceoverResult.body[1].duration;

    const bodyCStartTime = bodyBStartTime + bodyBDuration;
    const bodyCDuration = voiceoverResult.body[2].duration;

    const ctaStartTime = bodyCStartTime + bodyCDuration;
    const ctaDuration = voiceoverResult.cta.duration;

    const totalDuration =
      hookDuration +
      bodyADuration +
      bodyBDuration +
      bodyCDuration +
      ctaDuration;

    return {
      hookStartTime,
      hookDuration,
      bodyAStartTime,
      bodyADuration,
      bodyBStartTime,
      bodyBDuration,
      bodyCStartTime,
      bodyCDuration,
      ctaStartTime,
      ctaDuration,
      totalDuration,
    };
  }

  private generateCreatomateModifications(
    videoScript: VideoScript,
    voiceoverResult: VoiceoverResult,
    timing: DynamicTiming,
    videoSrc: string,
    image1Src: string,
    image2Src: string,
    ctaImageSrc: string,
  ): object {
    return {
      'BG-Video.source': videoSrc,
      'BG-Video.duration': timing.totalDuration + 1,

      'Image-1.source': image1Src,
      'Image-1.time': timing.bodyAStartTime,
      'Image-1.duration': timing.bodyADuration,

      'Image-2.source': image2Src,
      'Image-2.time': timing.bodyBStartTime,
      'Image-2.duration': timing.bodyBDuration,

      'CTA-Image.source': ctaImageSrc,
      'CTA-Image.time': timing.ctaStartTime,
      'CTA-Image.duration': timing.ctaDuration,

      'HOOK.text': videoScript.hook.voiceover,
      'HOOK.time': timing.hookStartTime,
      'HOOK.duration': timing.hookDuration,

      'BODY-A.text': videoScript.body[0].voiceover,
      'BODY-A.time': timing.bodyAStartTime,
      'BODY-A.duration': timing.bodyADuration,

      'BODY-B.text': videoScript.body[1].voiceover,
      'BODY-B.time': timing.bodyBStartTime,
      'BODY-B.duration': timing.bodyBDuration,

      'CTA.text': videoScript.cta.voiceover,
      'CTA.time': timing.ctaStartTime,
      'CTA.duration': timing.ctaDuration,

      'VO-HOOK.source': voiceoverResult.hook.url,
      'VO-HOOK.time': timing.hookStartTime,
      'VO-HOOK.duration': timing.hookDuration,

      'VO-A.source': voiceoverResult.body[0].url,
      'VO-A.time': timing.bodyAStartTime,
      'VO-A.duration': timing.bodyADuration,

      'VO-B.source': voiceoverResult.body[1].url,
      'VO-B.time': timing.bodyBStartTime,
      'VO-B.duration': timing.bodyBDuration,

      'VO-CTA.source': voiceoverResult.cta.url,
      'VO-CTA.time': timing.ctaStartTime,
      'VO-CTA.duration': timing.ctaDuration,
    };
  }

  private generateTemplate3Modifications(
    videoScript: VideoScriptTemplate3,
    voiceoverResult: VoiceoverResult,
    timing: DynamicTimingTemplate3,
    mediaSources: {
      videoSrc: string;
      image1Src: string;
      image2Src: string;
      image3Src: string;
    },
  ): CreatomateModifications {
    const modifications: CreatomateModifications = {};

    // Background videos
    modifications['Video-637.source'] = mediaSources.videoSrc;
    modifications['Video-CB6.source'] = mediaSources.videoSrc;
    modifications['Video-CB6.duration'] = timing.totalDuration + 1;

    // Images with dynamic timing
    modifications['Image-H2V.source'] = mediaSources.image1Src;
    modifications['Image-H2V.time'] = timing.bodyAStartTime;
    modifications['Image-H2V.duration'] = timing.bodyADuration;

    modifications['Image-H7X.source'] = mediaSources.image2Src;
    modifications['Image-H7X.time'] = timing.bodyBStartTime;
    modifications['Image-H7X.duration'] = timing.bodyBDuration;

    modifications['Image-2MB.source'] = mediaSources.image2Src;
    modifications['Image-2MB.time'] = timing.bodyBStartTime;
    modifications['Image-2MB.duration'] = timing.bodyBDuration;

    modifications['Image-NPN.source'] = mediaSources.image3Src;
    modifications['Image-NPN.time'] = timing.bodyCStartTime;
    modifications['Image-NPN.duration'] = timing.bodyCDuration;

    modifications['Image-PD3.source'] = mediaSources.image3Src;
    modifications['Image-PD3.time'] = timing.bodyCStartTime;
    modifications['Image-PD3.duration'] = timing.bodyCDuration;

    // Text segmentation for each section
    const hookSegments = this.segmentTextForSection(
      videoScript.hook.caption,
      ['Text-1-DCQ', 'Text-1', 'Text-1-XWZ', 'Text-1-8PL'],
      timing.hookStartTime,
      timing.hookDuration,
    );

    const bodyASegments = this.segmentTextForSection(
      videoScript.body[0].caption,
      ['Text-1-X56', 'Text-1-KFW', 'Text-1-HVN'],
      timing.bodyAStartTime,
      timing.bodyADuration,
    );

    // Body B has no text elements in template (visual only)

    const bodyCSegments = this.segmentTextForSection(
      videoScript.body[2].caption,
      ['Text-1-7W6', 'Text-1-XKD', 'Text-1-3XD', 'Text-1-6JV'],
      timing.bodyCStartTime,
      timing.bodyCDuration,
    );

    const ctaSegments = this.segmentTextForSection(
      videoScript.cta.caption,
      ['Text-1-5SS'],
      timing.ctaStartTime,
      timing.ctaDuration,
    );

    // Apply all text segments
    [
      ...hookSegments,
      ...bodyASegments,
      ...bodyCSegments,
      ...ctaSegments,
    ].forEach((segment) => {
      modifications[`${segment.elementId}.text`] = segment.text;
      modifications[`${segment.elementId}.time`] = segment.time;
      modifications[`${segment.elementId}.duration`] = segment.duration;
    });

    // Add separate voiceover tracks
    modifications['VO-HOOK.source'] = voiceoverResult.hook.url;
    modifications['VO-HOOK.time'] = timing.hookStartTime;
    modifications['VO-HOOK.duration'] = timing.hookDuration;

    modifications['VO-A.source'] = voiceoverResult.body[0].url;
    modifications['VO-A.time'] = timing.bodyAStartTime;
    modifications['VO-A.duration'] = timing.bodyADuration;

    modifications['VO-B.source'] = voiceoverResult.body[1].url;
    modifications['VO-B.time'] = timing.bodyBStartTime;
    modifications['VO-B.duration'] = timing.bodyBDuration;

    modifications['VO-C.source'] = voiceoverResult.body[2].url;
    modifications['VO-C.time'] = timing.bodyCStartTime;
    modifications['VO-C.duration'] = timing.bodyCDuration;

    modifications['VO-CTA.source'] = voiceoverResult.cta.url;
    modifications['VO-CTA.time'] = timing.ctaStartTime;
    modifications['VO-CTA.duration'] = timing.ctaDuration;

    return modifications;
  }

  async createVideoWithDynamicTiming(
    videoScript: VideoScript,
    mediaSources: {
      videoSrc: string;
      image1Src: string;
      image2Src: string;
      ctaImageSrc: string;
    },
  ) {
    const voiceoverResult = await this.createVoiceoverWithDuration(videoScript);

    const timing = this.calculateDynamicTiming(voiceoverResult);

    const modifications = this.generateCreatomateModifications(
      videoScript,
      voiceoverResult,
      timing,
      mediaSources.videoSrc,
      mediaSources.image1Src,
      mediaSources.image2Src,
      mediaSources.ctaImageSrc,
    );

    const options = {
      template_id: 'afd3b764-4923-4d88-87cd-c6bdd63638ae',
      modifications,
    };

    const response = await fetch(this.CMUrl, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${this.CMApiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(options),
    });

    const data = await response.json();

    console.log(data);

    return data;
  }

  async uploadVoiceover(buffer: Buffer<ArrayBuffer>, fileName: string) {
    const path = `/voiceovers/${fileName}`;
    const command = new PutObjectCommand({
      Bucket: 'likelionhugeteam',
      Key: path,
      Body: buffer,
      ContentType: 'audio/mpeg',
    });

    await this.s3Client.send(command);

    // Use longer expiry time to prevent URL expiration during video processing
    const url = await getSignedUrl(
      this.s3Client,
      new GetObjectCommand({
        Bucket: 'likelionhugeteam',
        Key: path,
      }),
      { expiresIn: 3600 * 24 }, // 24 hours instead of default 15 minutes
    );

    return url;
  }

  async createVideoWithTemplate3(
    videoScript: VideoScriptTemplate3,
    mediaSources: {
      videoSrc: string;
      image1Src: string;
      image2Src: string;
      image3Src: string;
    },
  ) {
    // Validate videoScript has 3 body sections
    if (videoScript.body.length !== 3) {
      throw new Error(
        'VideoScript must have exactly 3 body sections for Template 3',
      );
    }

    // Use existing voiceover creation (no changes needed)
    const voiceoverResult = await this.createVoiceoverWithDuration(videoScript);

    // Use extended timing calculation for body[2]
    const timing = this.calculateDynamicTimingTemplate3(voiceoverResult);

    // Generate Template 3 specific modifications
    const modifications = this.generateTemplate3Modifications(
      videoScript,
      voiceoverResult,
      timing,
      mediaSources,
    );

    const options = {
      template_id: 'c5a2f519-2991-4398-9ad8-d16176637fcd', // Template 3 ID
      modifications,
    };

    const response = await fetch(this.CMUrl, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${this.CMApiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(options),
    });

    if (!response.ok) {
      throw new Error(
        `Creatomate API error: ${response.status} ${response.statusText}`,
      );
    }

    const data = await response.json();
    console.log('Template 3 video creation response:', data);
    return data;
  }
}

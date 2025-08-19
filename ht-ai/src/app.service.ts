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
  VoiceoverResult,
  AudioWithDuration,
  DynamicTiming,
} from './types';
import { parseBuffer } from 'music-metadata';
import Creatomate from 'creatomate';
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
    // const response = await this.openAiClient.responses.create({
    //   model: 'gpt-5-nano-2025-08-07',
    //   reasoning: { effort: 'minimal' },
    //   input: [
    //     {
    //       role: 'system',
    //       content: SYSTEM_IMAGE_ANALYSIS,
    //     },
    //     {
    //       role: 'user',
    //       content: [
    //         ...imageUrls.map((imageUrl) => ({
    //           type: 'input_image' as const,
    //           image_url: imageUrl,
    //           detail: 'auto' as const,
    //         })),
    //         {
    //           type: 'input_text' as const,
    //           text: userImageAnalysisPrompt(text, imageUrls),
    //         },
    //       ] as const,
    //     },
    //   ],
    //   max_output_tokens: 400,
    // });

    // console.log(JSON.parse(response.output_text));

    // return JSON.parse(response.output_text) as ImageAnalysis;

    return {
      imageDescriptions: [
        {
          description:
            '송풍 구운 스테이크와 다진 파가 올려진 반달 접시, 고소한 소스와 훈연 느낌의 데판 스타일 분위기.',
          caption: '파가 올린 반접시 데판의 매력',
        },
        {
          description:
            '둥근 나무 토기에 담긴 덮밥 위에 연어와 장어가 층층이 얹혀 있고 밥이 보이는 비주얼.',
          caption: '천상의 밥상, 복돌이의 가게',
        },
      ],
    };
  }

  async createVideoScript(
    imageAnalysis: ImageAnalysis,
    text: string,
  ): Promise<VideoScript> {
    // const response = await this.openAiClient.responses.create({
    //   model: 'gpt-5-nano-2025-08-07',
    //   reasoning: { effort: 'minimal' },
    //   input: [
    //     {
    //       role: 'system',
    //       content: SYSTEM_VIDEO_SCRIPT,
    //     },
    //     {
    //       role: 'user',
    //       content: [
    //         ...imageAnalysis.imageDescriptions.map((description) => {
    //           return {
    //             type: 'input_text' as const,
    //             text: `description:${description.description},caption:${description.caption}`,
    //           };
    //         }),
    //         {
    //           type: 'input_text' as const,
    //           text,
    //         },
    //       ] as const,
    //     },
    //   ],
    //   max_output_tokens: 1000,
    // });

    // console.log(JSON.parse(response.output_text));

    // return JSON.parse(response.output_text) as VideoScript;

    return {
      hook: {
        caption: '파가 올린 반접시 매력',
        voiceover: '파가 올린 반접시의 매력에 푹 빠지게 될 거예요',
      },
      body: [
        {
          caption: '데판의 고소한 풍미, 소스까지 한입에',
          voiceover:
            '데판의 고소한 풍미가 입안에서 퍼지면서 소스의 깊이가 느껴져요',
        },
        {
          caption: '천상의 밥상, 한입의 마법',
          voiceover: '둥근 그릇에 담긴 층층이 비주얼과 향이 입끝에서 춤춰요',
        },
      ],
      cta: {
        caption: '여긴 저장각',
        voiceover:
          '지금 바로 저장하고 친구와 공유해 보세요, 신촌의 복돌이의 가게를 방문해요',
      },
    };
  }

  async createVoiceoverWithDuration(
    videoScript: VideoScript,
  ): Promise<VoiceoverResult> {
    const hookVoiceover = await this.openAiClient.audio.speech.create({
      model: 'gpt-4o-mini-tts',
      voice: 'echo',
      input: videoScript.hook.voiceover,
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
          voice: 'echo',
          input: b.voiceover,
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
      voice: 'echo',
      input: videoScript.cta.voiceover,
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

    const url = await getSignedUrl(
      this.s3Client,
      new GetObjectCommand({
        Bucket: 'likelionhugeteam',
        Key: path,
      }),
    );

    return url;
  }

  async createVideoRequest(
    videoSrc: string,
    image1Src: string,
    image2Src: string,
    ctaImageSrc: string,
    hookVO: string,
    VO1: string,
    VO2: string,
    ctaVO: string,
    hookText: string,
    VO1Text: string,
    VO2Text: string,
    ctaText: string,
  ) {
    const options = {
      template_id: 'afd3b764-4923-4d88-87cd-c6bdd63638ae',
      modifications: {
        'BG-Video.source': videoSrc,
        'Image-1.source': image1Src,
        'Image-2.source': image2Src,
        'CTA-Image.source': ctaImageSrc,
        'HOOK.text': hookText,
        'BODY-A.text': VO1Text,
        'BODY-B.text': VO2Text,
        'CTA.text': ctaText,
        'VO-HOOK.source': hookVO,
        'VO-A.source': VO1,
        'VO-B.source': VO2,
        'VO-CTA.source': ctaVO,
      },
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
}

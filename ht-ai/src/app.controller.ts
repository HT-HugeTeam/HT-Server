import { Controller, Post, Body } from '@nestjs/common';
import { AppService } from './app.service';
import type { CreateVideoDto } from './create-video.dto';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Post()
  async createVideo(@Body() createVideoDto: CreateVideoDto) {
    const { image1Url, image2Url, image3Url, videoUrl, text } = createVideoDto;

    const imageAnalysis = await this.appService.analyzeImage(
      [image1Url, image2Url],
      text,
    );

    const videoScript = await this.appService.createVideoScript(
      imageAnalysis,
      text,
    );

    const videoResponse = await this.appService.createVideoWithDynamicTiming(
      videoScript,
      {
        videoSrc: videoUrl,
        image1Src: image1Url,
        image2Src: image2Url,
        ctaImageSrc: image3Url,
      },
    );

    console.log(videoResponse);

    return videoResponse;
  }

  @Post('template3')
  async createVideoTemplate3(@Body() createVideoDto: CreateVideoDto) {
    const { image1Url, image2Url, image3Url, videoUrl, text } = createVideoDto;

    const imageAnalysis = await this.appService.analyzeImage(
      [image1Url, image2Url, image3Url],
      text,
    );

    const videoScript = await this.appService.createVideoScriptTemplate3(
      imageAnalysis,
      text,
    );

    const videoResponse = await this.appService.createVideoWithTemplate3(
      videoScript,
      {
        videoSrc: videoUrl,
        image1Src: image1Url,
        image2Src: image2Url,
        image3Src: image3Url,
      },
    );

    return videoResponse;
  }
}

import { Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import { videos } from 'twelvelabs-js/api/resources/indexes';

const image1 =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_4546.jpeg?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjELn%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgDFvqd1SERIMKoN6CMCS5TBKucJ3WlaLLJZYzNWzMDZUCIBG0v8ZL%2FWKUXNsMYjxzHkK65LUFF4wRCTsIv1IzqP%2FNKrkDCBIQABoMNjUwMjUxNzMwNjQ5IgzAi6VUztsUJ8uNH28qlgMxo1o6w8mf8RiF57INygQMOi%2BKCawCAQulUYYgQbOfh7yZt6wfk9GajZBaSNDh74e3Y6qpcHEu1l%2FhIE3lg%2FOxr9aFBMyFEV0k9lWgjjvBtGt7KK33ri4VFwh3gycm0uNlqeafUZGraHuSwrw2RZB9f%2F2pBgor9ecqkWzCPfnZ4PYjeV4TLsEL%2FVJwwYP1rou9p5EfVInPOOAvkHLUWTCMKzOX4RicDZeQ%2FsvzHmdhwFt%2BFyQvmePnXDEJ6dIiCfqZ3ZuhcpKydDhJsYykzkGq99LIKvoXVmPBL899NY9sCI9KeE6IhVLdKp9Y4vi2fz628lj%2Fq%2B%2FxO9r2Ixr%2FhWRUEchm2xTbOvIHjWoTgX7VLWQXQHVR7YDZT8FOxLhzot7FLko%2B7U8wuyhepXt52Jb5RX%2FmcEZKfI7ebJVtuGwn%2F8YMPyWLS9AZytw%2FoIWJ0kRCfzP8aIc6%2BsCewEm0criauy8yenEEBOsJkfIaAP1uwVEC6bPfEPCyJO7%2BTOHBh8j16AtFTXDRh4qWmarCpKH0ntFhNdY3ML%2FYoMUGOt8CTTvOBrFHbSNIyunbfNjODHY715aI75%2Bf6hYd3Lw9%2B1wfrLzjPjY9iTCGWO70%2F4hD56dWL3%2Fs41IMV8oj56l%2BZi6B4WI71e2%2Fd%2BWXITvjJa8s7mNU0UcbULLTWRjP4dVakUPi0xaEQBtVs6MhaTcb3uvgcoTAfhRyfiAfqdiIgtdM%2FCe%2BBRzMj4atFkHNteGqZ6LG4%2FyPMvbuXntGSlAm4tOtmy8bwI%2FDV3eeH1nWLfJ0VX%2FAs%2BzYGvb%2FswWCJBv9ipZa9H8wbpziRqxqZeV0Oag4H1rXpqZjtCetjY8cX3z1XAC56R3BYHdjYax7%2FqSOah6o80GnmMwxHo3VmmVT0%2B4VPUiGkj60w12IlR46kFT23HuvrQQ6vFJP6Xg4fdP9f90FpgyVMldODraLRDOW6vbVXigp4bY6biW%2Bnl3kl1XPiV6PaFtlAWWpK60CvaTveF%2BZTHZzkgo0qkJkF84O&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73M5EXNAWVF%2F20250822%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250822T083755Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=5afa4b520ad441dadc8195a8edddc4bb4d989d8c2248b4b7b19d7d53be610afe';

const image2 =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_5008.jpg?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjELn%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgDFvqd1SERIMKoN6CMCS5TBKucJ3WlaLLJZYzNWzMDZUCIBG0v8ZL%2FWKUXNsMYjxzHkK65LUFF4wRCTsIv1IzqP%2FNKrkDCBIQABoMNjUwMjUxNzMwNjQ5IgzAi6VUztsUJ8uNH28qlgMxo1o6w8mf8RiF57INygQMOi%2BKCawCAQulUYYgQbOfh7yZt6wfk9GajZBaSNDh74e3Y6qpcHEu1l%2FhIE3lg%2FOxr9aFBMyFEV0k9lWgjjvBtGt7KK33ri4VFwh3gycm0uNlqeafUZGraHuSwrw2RZB9f%2F2pBgor9ecqkWzCPfnZ4PYjeV4TLsEL%2FVJwwYP1rou9p5EfVInPOOAvkHLUWTCMKzOX4RicDZeQ%2FsvzHmdhwFt%2BFyQvmePnXDEJ6dIiCfqZ3ZuhcpKydDhJsYykzkGq99LIKvoXVmPBL899NY9sCI9KeE6IhVLdKp9Y4vi2fz628lj%2Fq%2B%2FxO9r2Ixr%2FhWRUEchm2xTbOvIHjWoTgX7VLWQXQHVR7YDZT8FOxLhzot7FLko%2B7U8wuyhepXt52Jb5RX%2FmcEZKfI7ebJVtuGwn%2F8YMPyWLS9AZytw%2FoIWJ0kRCfzP8aIc6%2BsCewEm0criauy8yenEEBOsJkfIaAP1uwVEC6bPfEPCyJO7%2BTOHBh8j16AtFTXDRh4qWmarCpKH0ntFhNdY3ML%2FYoMUGOt8CTTvOBrFHbSNIyunbfNjODHY715aI75%2Bf6hYd3Lw9%2B1wfrLzjPjY9iTCGWO70%2F4hD56dWL3%2Fs41IMV8oj56l%2BZi6B4WI71e2%2Fd%2BWXITvjJa8s7mNU0UcbULLTWRjP4dVakUPi0xaEQBtVs6MhaTcb3uvgcoTAfhRyfiAfqdiIgtdM%2FCe%2BBRzMj4atFkHNteGqZ6LG4%2FyPMvbuXntGSlAm4tOtmy8bwI%2FDV3eeH1nWLfJ0VX%2FAs%2BzYGvb%2FswWCJBv9ipZa9H8wbpziRqxqZeV0Oag4H1rXpqZjtCetjY8cX3z1XAC56R3BYHdjYax7%2FqSOah6o80GnmMwxHo3VmmVT0%2B4VPUiGkj60w12IlR46kFT23HuvrQQ6vFJP6Xg4fdP9f90FpgyVMldODraLRDOW6vbVXigp4bY6biW%2Bnl3kl1XPiV6PaFtlAWWpK60CvaTveF%2BZTHZzkgo0qkJkF84O&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73M5EXNAWVF%2F20250822%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250822T083831Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=84659d0341c6ab2bed4458a7750a086b75bdb90a744f8f8814d3e0569e8a9f12';

const video =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_8740.MOV?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjELn%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgDFvqd1SERIMKoN6CMCS5TBKucJ3WlaLLJZYzNWzMDZUCIBG0v8ZL%2FWKUXNsMYjxzHkK65LUFF4wRCTsIv1IzqP%2FNKrkDCBIQABoMNjUwMjUxNzMwNjQ5IgzAi6VUztsUJ8uNH28qlgMxo1o6w8mf8RiF57INygQMOi%2BKCawCAQulUYYgQbOfh7yZt6wfk9GajZBaSNDh74e3Y6qpcHEu1l%2FhIE3lg%2FOxr9aFBMyFEV0k9lWgjjvBtGt7KK33ri4VFwh3gycm0uNlqeafUZGraHuSwrw2RZB9f%2F2pBgor9ecqkWzCPfnZ4PYjeV4TLsEL%2FVJwwYP1rou9p5EfVInPOOAvkHLUWTCMKzOX4RicDZeQ%2FsvzHmdhwFt%2BFyQvmePnXDEJ6dIiCfqZ3ZuhcpKydDhJsYykzkGq99LIKvoXVmPBL899NY9sCI9KeE6IhVLdKp9Y4vi2fz628lj%2Fq%2B%2FxO9r2Ixr%2FhWRUEchm2xTbOvIHjWoTgX7VLWQXQHVR7YDZT8FOxLhzot7FLko%2B7U8wuyhepXt52Jb5RX%2FmcEZKfI7ebJVtuGwn%2F8YMPyWLS9AZytw%2FoIWJ0kRCfzP8aIc6%2BsCewEm0criauy8yenEEBOsJkfIaAP1uwVEC6bPfEPCyJO7%2BTOHBh8j16AtFTXDRh4qWmarCpKH0ntFhNdY3ML%2FYoMUGOt8CTTvOBrFHbSNIyunbfNjODHY715aI75%2Bf6hYd3Lw9%2B1wfrLzjPjY9iTCGWO70%2F4hD56dWL3%2Fs41IMV8oj56l%2BZi6B4WI71e2%2Fd%2BWXITvjJa8s7mNU0UcbULLTWRjP4dVakUPi0xaEQBtVs6MhaTcb3uvgcoTAfhRyfiAfqdiIgtdM%2FCe%2BBRzMj4atFkHNteGqZ6LG4%2FyPMvbuXntGSlAm4tOtmy8bwI%2FDV3eeH1nWLfJ0VX%2FAs%2BzYGvb%2FswWCJBv9ipZa9H8wbpziRqxqZeV0Oag4H1rXpqZjtCetjY8cX3z1XAC56R3BYHdjYax7%2FqSOah6o80GnmMwxHo3VmmVT0%2B4VPUiGkj60w12IlR46kFT23HuvrQQ6vFJP6Xg4fdP9f90FpgyVMldODraLRDOW6vbVXigp4bY6biW%2Bnl3kl1XPiV6PaFtlAWWpK60CvaTveF%2BZTHZzkgo0qkJkF84O&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73M5EXNAWVF%2F20250822%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250822T083854Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=964a946ce84d0eceffe83f4b36b03f89dec9cb12fa24360a793fe2a93c172709';

const text = `일식, 중식, 양식, 모든 음식을 제공하는 최고의 레스토랑!

중국 현지에서 먹는 것 같은 데판야끼와, 일본 현지에서 먹는 것 같은 명란 장어 덮밥, 그리고 아무 곳에서도 먹을 수 없는 천상의 함바그까지!

오로지 신촌 “복돌이의 가게”에서만 먹어볼 수 있다!`;

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Post()
  async createVideo() {
    const imageAnalysis = await this.appService.analyzeImage(
      [image1, image2],
      text,
    );

    const videoScript = await this.appService.createVideoScript(
      imageAnalysis,
      text,
    );

    const videoResponse = await this.appService.createVideoWithDynamicTiming(
      videoScript,
      {
        videoSrc: video,
        image1Src: image1,
        image2Src: image2,
        ctaImageSrc: image1,
      },
    );

    return videoResponse;
  }

  @Post('template3')
  async createVideoTemplate3() {
    const imageAnalysis = await this.appService.analyzeImage(
      [image1, image2, image1],
      text,
    );

    const videoScript = await this.appService.createVideoScriptTemplate3(
      imageAnalysis,
      text,
    );

    const videoResponse = await this.appService.createVideoWithTemplate3(
      videoScript,
      {
        videoSrc: video,
        image1Src: image1,
        image2Src: image2,
        image3Src: image1,
      },
    );

    return videoResponse;
  }
}

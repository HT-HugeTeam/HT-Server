import { Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import { videos } from 'twelvelabs-js/api/resources/indexes';

const image1 =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_4546.jpeg?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgAbC1X3jLPfFYiP5ZRGlRUPHQIAmYxMsVT4JtEqwDXE8CIDtzew415xXPsSZ4QO6jnajym54BNmktoQeJJ7D2fGd2KrkDCFoQABoMNjUwMjUxNzMwNjQ5IgwvZlsN8rjtu8qOZSoqlgOL%2FnKL4Y6WhitYVS2LTWI3OrEGOZRunIuyaxplOaDeGgBpXWxCueglt4FDXn6bJquxcSI4bJib86NcEnleRGkCzdboStbuIx76ptWJcSQpd1%2FO%2BEfG0rzO9dNPjKXjNd0AcvFqjpUVgWdcVrEwl0z5UnvNZbMkq2nsITFJ84dGO289nLyl1mc7EqVL9kngG1Tr4r7ru21o7psynja0XL%2F0bXLZ4jP1XiY0J9xI2xkbLC1EbPl4KpxgYBy0S7CKsU7H0qA53kvZvvvzpZXuEO0aehRQejyD43Ubv8dCuCrXHQeOzO%2BEvS%2FmVnKUaeQu1yKUSnCpv0AoOX33wCzeIkAW9Ycgowm2%2B48oK7kP18SJiVS31qccJToGJNpGNKsKExWYxakCBiMwSXnhZ8JWXjyQWblf%2F0vg1lCAgEs7QJO%2Bf0ziifF7XclZlmIzWqJU%2BPbgzBosw7jdkkbJCIjfRjjUXx9WgSbPCGxTNJyM7RB9q0xk4AX4nYM6qFLLfb3Tq3eC7ZruLcqgPL3hrFnIc513B9vfFMDRMOiL%2B8QGOt8CnGhhAdMkswwXkparkyxAJPeWnnHohK2y1XOTDWXGjWqzK%2B0z1LVztEmFx1TQz1BgKyUvmyoCtxp7qqBRv9IFWwZg4%2FntbnZc%2BImBmNqPM3coaR6zb41HIq3oG5HKG5GUX3vwB0cPc%2Bjn9n3ddiG7IMV6RW%2Fw0IjF7IIC93EUT1qNKncv9mTsa6GcJFsBmlS9LCHHM8GEVROr4jtEONAfOIUy%2BCfQCCrY5eU1HetePJb7pZNqH4FvGZE%2FMBJ5N002YcKORUHNaZeFkcPp8JtnhwobKE1wYG38zhq8sEBgKAsfywe98fVO6Ehds1lK18AVt3GUxWYaut14uiv8sAqOmJYw%2FVW73rI%2FUeTy%2B%2Bmejgp9k4hfUvcCM%2FeanfE0ljB7nMDjXDE%2BL5yIZn0az2e4BFOc7X3UNEwVPCn3LVJegYEBE1I8e6TuFJO7d4kFobK0f9w%2BMI%2FkYiErkkq%2Bc6zA&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73MZQ2SPCJJ%2F20250815%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250815T083610Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=147c839b35080ee4f93b0125e679f089e65c8bed8d3facba9dd35b94ec484823';

const image2 =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_5008.jpg?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgAbC1X3jLPfFYiP5ZRGlRUPHQIAmYxMsVT4JtEqwDXE8CIDtzew415xXPsSZ4QO6jnajym54BNmktoQeJJ7D2fGd2KrkDCFoQABoMNjUwMjUxNzMwNjQ5IgwvZlsN8rjtu8qOZSoqlgOL%2FnKL4Y6WhitYVS2LTWI3OrEGOZRunIuyaxplOaDeGgBpXWxCueglt4FDXn6bJquxcSI4bJib86NcEnleRGkCzdboStbuIx76ptWJcSQpd1%2FO%2BEfG0rzO9dNPjKXjNd0AcvFqjpUVgWdcVrEwl0z5UnvNZbMkq2nsITFJ84dGO289nLyl1mc7EqVL9kngG1Tr4r7ru21o7psynja0XL%2F0bXLZ4jP1XiY0J9xI2xkbLC1EbPl4KpxgYBy0S7CKsU7H0qA53kvZvvvzpZXuEO0aehRQejyD43Ubv8dCuCrXHQeOzO%2BEvS%2FmVnKUaeQu1yKUSnCpv0AoOX33wCzeIkAW9Ycgowm2%2B48oK7kP18SJiVS31qccJToGJNpGNKsKExWYxakCBiMwSXnhZ8JWXjyQWblf%2F0vg1lCAgEs7QJO%2Bf0ziifF7XclZlmIzWqJU%2BPbgzBosw7jdkkbJCIjfRjjUXx9WgSbPCGxTNJyM7RB9q0xk4AX4nYM6qFLLfb3Tq3eC7ZruLcqgPL3hrFnIc513B9vfFMDRMOiL%2B8QGOt8CnGhhAdMkswwXkparkyxAJPeWnnHohK2y1XOTDWXGjWqzK%2B0z1LVztEmFx1TQz1BgKyUvmyoCtxp7qqBRv9IFWwZg4%2FntbnZc%2BImBmNqPM3coaR6zb41HIq3oG5HKG5GUX3vwB0cPc%2Bjn9n3ddiG7IMV6RW%2Fw0IjF7IIC93EUT1qNKncv9mTsa6GcJFsBmlS9LCHHM8GEVROr4jtEONAfOIUy%2BCfQCCrY5eU1HetePJb7pZNqH4FvGZE%2FMBJ5N002YcKORUHNaZeFkcPp8JtnhwobKE1wYG38zhq8sEBgKAsfywe98fVO6Ehds1lK18AVt3GUxWYaut14uiv8sAqOmJYw%2FVW73rI%2FUeTy%2B%2Bmejgp9k4hfUvcCM%2FeanfE0ljB7nMDjXDE%2BL5yIZn0az2e4BFOc7X3UNEwVPCn3LVJegYEBE1I8e6TuFJO7d4kFobK0f9w%2BMI%2FkYiErkkq%2Bc6zA&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73MZQ2SPCJJ%2F20250815%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250815T083628Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=6fc217ce360a9f6226b809592c8fae81becb267b825ce21880f6cf26345fb0fa';

const video =
  'https://likelionhugeteam.s3.ap-northeast-2.amazonaws.com/IMG_8740.MOV?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgAbC1X3jLPfFYiP5ZRGlRUPHQIAmYxMsVT4JtEqwDXE8CIDtzew415xXPsSZ4QO6jnajym54BNmktoQeJJ7D2fGd2KrkDCFoQABoMNjUwMjUxNzMwNjQ5IgwvZlsN8rjtu8qOZSoqlgOL%2FnKL4Y6WhitYVS2LTWI3OrEGOZRunIuyaxplOaDeGgBpXWxCueglt4FDXn6bJquxcSI4bJib86NcEnleRGkCzdboStbuIx76ptWJcSQpd1%2FO%2BEfG0rzO9dNPjKXjNd0AcvFqjpUVgWdcVrEwl0z5UnvNZbMkq2nsITFJ84dGO289nLyl1mc7EqVL9kngG1Tr4r7ru21o7psynja0XL%2F0bXLZ4jP1XiY0J9xI2xkbLC1EbPl4KpxgYBy0S7CKsU7H0qA53kvZvvvzpZXuEO0aehRQejyD43Ubv8dCuCrXHQeOzO%2BEvS%2FmVnKUaeQu1yKUSnCpv0AoOX33wCzeIkAW9Ycgowm2%2B48oK7kP18SJiVS31qccJToGJNpGNKsKExWYxakCBiMwSXnhZ8JWXjyQWblf%2F0vg1lCAgEs7QJO%2Bf0ziifF7XclZlmIzWqJU%2BPbgzBosw7jdkkbJCIjfRjjUXx9WgSbPCGxTNJyM7RB9q0xk4AX4nYM6qFLLfb3Tq3eC7ZruLcqgPL3hrFnIc513B9vfFMDRMOiL%2B8QGOt8CnGhhAdMkswwXkparkyxAJPeWnnHohK2y1XOTDWXGjWqzK%2B0z1LVztEmFx1TQz1BgKyUvmyoCtxp7qqBRv9IFWwZg4%2FntbnZc%2BImBmNqPM3coaR6zb41HIq3oG5HKG5GUX3vwB0cPc%2Bjn9n3ddiG7IMV6RW%2Fw0IjF7IIC93EUT1qNKncv9mTsa6GcJFsBmlS9LCHHM8GEVROr4jtEONAfOIUy%2BCfQCCrY5eU1HetePJb7pZNqH4FvGZE%2FMBJ5N002YcKORUHNaZeFkcPp8JtnhwobKE1wYG38zhq8sEBgKAsfywe98fVO6Ehds1lK18AVt3GUxWYaut14uiv8sAqOmJYw%2FVW73rI%2FUeTy%2B%2Bmejgp9k4hfUvcCM%2FeanfE0ljB7nMDjXDE%2BL5yIZn0az2e4BFOc7X3UNEwVPCn3LVJegYEBE1I8e6TuFJO7d4kFobK0f9w%2BMI%2FkYiErkkq%2Bc6zA&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAZOZQF73MZQ2SPCJJ%2F20250815%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20250815T083641Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=31d8863c3f3b0029c60a85676299b6d8633f6065843e0273c2992ef140ac1d9c';

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
      }
    );

    return videoResponse;
  }
}

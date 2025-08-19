export interface ImageAnalysis {
  imageDescriptions: {
    description: string;
    caption: string;
  }[];
}

export interface VideoScript {
  hook: { caption: string; voiceover: string };
  body: [
    { caption: string; voiceover: string },
    { caption: string; voiceover: string },
  ];
  cta: { caption: string; voiceover: string };
}

export interface AudioWithDuration {
  url: string;
  duration: number;
}

export interface VoiceoverResult {
  hook: AudioWithDuration;
  body: AudioWithDuration[];
  cta: AudioWithDuration;
}

export interface DynamicTiming {
  hookStartTime: number;
  hookDuration: number;
  bodyAStartTime: number;
  bodyADuration: number;
  bodyBStartTime: number;
  bodyBDuration: number;
  ctaStartTime: number;
  ctaDuration: number;
  totalDuration: number;
}

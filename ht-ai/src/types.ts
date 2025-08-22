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

export interface VideoScriptTemplate3 {
  hook: { caption: string; voiceover: string };
  body: [
    { caption: string; voiceover: string }, // menu1
    { caption: string; voiceover: string }, // menu2
    { caption: string; voiceover: string }, // atmosphere
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

export interface DynamicTimingTemplate3 {
  hookStartTime: number;
  hookDuration: number;
  bodyAStartTime: number;
  bodyADuration: number;
  bodyBStartTime: number;
  bodyBDuration: number;
  bodyCStartTime: number;
  bodyCDuration: number;
  ctaStartTime: number;
  ctaDuration: number;
  totalDuration: number;
}

export interface TextSegment {
  elementId: string;
  text: string;
  time: number;
  duration: number;
}

export interface CreatomateModifications {
  [key: string]: string | number;
}

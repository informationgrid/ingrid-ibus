import { SearchHit } from './SearchHit';

export class DebugEvent {
  title: string;
  message: string;
  messageList: string[];
  duration: number;
}

export class SearchHits {
  length: number;
  hits: SearchHit[];
  debug: DebugEvent[];
}

export class SearchHit {
  id: string;
  indexId: string;
  title: string;
  summary: string;
  source: string;
  detail: string;
  dataSourceName: string;

  es_index: string;
  es_type: string;
  hitDetail: any;
}

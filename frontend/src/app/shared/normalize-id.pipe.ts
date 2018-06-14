import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'normalizeId'
})
export class NormalizeIdPipe implements PipeTransform {

  transform(value: string, args?: any): any {
    return value.replace('/', '').replace(':', '');
  }

}

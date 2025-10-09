/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import { Pipe, PipeTransform } from '@angular/core';

// Epochs
const epochs: any[] = [
  ['Jahr', 31536000, 'en'],
  ['Monat', 2592000, 'en'],
  ['Tag', 86400, 'en'],
  ['Stunde', 3600, 'n'],
  ['Minute', 60, 'n'],
  ['Sekunde', 1, 'n']
];

@Pipe({
    name: 'dateRelative',
    standalone: false
})
export class DateRelativePipe implements PipeTransform {

  getDuration(timeAgoInSeconds: number) {
    for (let [name, seconds, plural] of epochs) {
      let interval = Math.floor(timeAgoInSeconds / seconds);

      if (interval >= 1) {

        // add next epoch for more detailed information
        let nextTimeAgoInSeconds = timeAgoInSeconds - (interval * seconds);
        let postInterval = this.getDuration(nextTimeAgoInSeconds).interval;
        /*if (postInterval !== 0) {
          postInterval = ':' + ((postInterval < 10) ? '0' + postInterval : postInterval + '');
        } else {
          postInterval = '';
        }*/
        let suffix = interval === 1 ? '' : plural;

        return {
          interval: interval + ' ' + name + suffix + (postInterval === 0 ? '' : ' ' + postInterval),
          epoch: name,
          plural: plural
        };
      }
    }
    return {
      interval: 0,
      epoch: 'Sekunde',
      plural: 'n'
    };
  };

  transform(dateStamp: string): string {

    if (!dateStamp) { return '???' }
    let timeAgoInSeconds = Math.floor((new Date().getTime() - new Date(dateStamp).getTime()) / 1000);
    let {interval, epoch, plural} = this.getDuration(timeAgoInSeconds);
    // let suffix = interval === 1 ? '' : plural;

    return `vor ${interval}`;

  }

}

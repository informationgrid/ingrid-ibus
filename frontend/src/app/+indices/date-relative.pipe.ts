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
  name: 'dateRelative'
})
export class DateRelativePipe implements PipeTransform {

  getDetailedDuration(timeAgoInSeconds: number) {

  }

  getDuration(timeAgoInSeconds: number) {
    for (let [name, seconds, plural] of epochs) {
      let interval = Math.floor(timeAgoInSeconds / seconds);

      if (interval >= 1) {

        // add next epoch for more detailed information
        /*let postInterval = '';
        let index = epochs.findIndex( epoch => epoch[0] === name);
        if (index !== epochs.length - 1) {
          let [nextName, nextSeconds] = epochs[index + 1];
          let nextTimeAgoInSeconds = timeAgoInSeconds - (interval * seconds);
          let nextInterval = Math.floor(nextTimeAgoInSeconds / nextSeconds);
          postInterval = ':' + ((nextInterval < 10) ? '0' + nextInterval : nextInterval + '');
        }*/
        let nextTimeAgoInSeconds = timeAgoInSeconds - (interval * seconds);
        let postInterval = this.getDuration(nextTimeAgoInSeconds).interval;
        if (postInterval !== 0) {
          postInterval = ':' + ((postInterval < 10) ? '0' + postInterval : postInterval + '');
        } else {
          postInterval = '';
        }

        return {
          interval: interval + postInterval,
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

  transform(dateStamp: number): string {

    let timeAgoInSeconds = Math.floor((new Date().getTime() - new Date(dateStamp).getTime()) / 1000);
    let {interval, epoch, plural} = this.getDuration(timeAgoInSeconds);
    let suffix = interval === 1 ? '' : plural;

    return `vor ${interval} ${epoch}${suffix}`;

  }

}

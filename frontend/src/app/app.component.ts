import {Component} from '@angular/core';
import {ConfigService} from './config.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  showMenu = false;
  showIndicesPage = true;

  constructor(private config: ConfigService) {
    if (config.getConfiguration().useIndices === false) {
      this.showIndicesPage = false;
    }
  }

}

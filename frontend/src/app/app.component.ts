import {Component} from '@angular/core';
import {ConfigService} from './config.service';
import {SettingsService} from './+settings/settings.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  showMenu = false;
  showIndicesPage = true;

  constructor(private config: ConfigService, settingsService: SettingsService, router: Router) {
    if (config.getConfiguration().useIndices === false) {
      this.showIndicesPage = false;
    }
    settingsService.get().subscribe( appCfg => {
      if (appCfg['needPasswordChange'] === 'true') {
        router.navigate(['/settings']);
      }
    })
  }

}

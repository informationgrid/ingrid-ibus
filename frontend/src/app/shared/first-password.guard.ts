import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import {AppConfiguation, SettingsService} from '../+settings/settings.service';

@Injectable({
  providedIn: 'root'
})
export class FirstPasswordGuard implements CanActivate {

  private config: AppConfiguation;

  constructor(private settingsService: SettingsService) {
    this.settingsService.appConfiguration.subscribe( appCfg => {
      this.config = appCfg;
    });
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    if (this.config['needPasswordChange'] === 'true' && next.url[0].path !== 'settings') {
      return false;
    }
    return true;
  }
}

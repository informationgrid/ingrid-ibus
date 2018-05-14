import {AppModule} from './app/app.module';
import {environment} from './environments/environment';
import {enableProdMode, NgModuleRef} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {hmrBootstrap} from './hmr';

if (environment.production) {
  enableProdMode();
}

const bootstrap = () => <Promise<NgModuleRef<AppModule>>>platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.log(err));

if (environment.hmr) {
  if (module['hot']) {
    hmrBootstrap(module, bootstrap);
  } else {
    console.error('HMR is not enabled for webpack-dev-server!');
    console.log('Are you using the --hmr flag for ng serve?');
  }
} else {
  bootstrap();
}

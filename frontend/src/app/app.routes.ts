import { SearchDetailComponent } from './+search/+search-detail/search-detail.component';
import { SearchComponent } from './+search/search/search.component';
import { IndexDetailComponent } from './+indices/+index-detail/index-detail.component';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { RouterModule, Routes } from '@angular/router';
import { SettingsListComponent } from './+settings/settings-list/settings-list.component';
import { ConnectedIplugsComponent } from './+iplugs/connected-iplugs/connected-iplugs.component';
import { environment } from '../environments/environment';
import {FirstPasswordGuard} from './shared/first-password.guard';

const appRoutes: Routes = [
  {path: 'iplugs', component: ConnectedIplugsComponent, canActivate: [FirstPasswordGuard]},
  {path: 'indices', component: ListIndicesComponent, canActivate: [FirstPasswordGuard]},
  {path: 'indices/:id', component: IndexDetailComponent, canActivate: [FirstPasswordGuard]},
  {path: 'search', component: SearchComponent, canActivate: [FirstPasswordGuard]},
  {path: 'search/:indexId/:hitId', component: SearchDetailComponent, canActivate: [FirstPasswordGuard]},
  {path: 'settings', component: SettingsListComponent},
  {
    path: '',
    redirectTo: '/iplugs',
    pathMatch: 'full'
  }
  // { path: '**', component: PageNotFoundComponent }
];

export let rootRoutes = RouterModule.forRoot(
  appRoutes,
  {
    enableTracing: !environment.production,  // <-- debugging purposes only
    useHash: true
  }
);

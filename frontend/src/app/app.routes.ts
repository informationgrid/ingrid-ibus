import { SearchDetailComponent } from './+search/+search-detail/search-detail.component';
import { SearchComponent } from './+search/search/search.component';
import { IndexDetailComponent } from './+indices/+index-detail/index-detail.component';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { RouterModule, Routes } from '@angular/router';
import { SettingsListComponent } from './+settings/settings-list/settings-list.component';
import { environment } from '../environments/environment';

const appRoutes: Routes = [
  { path: 'indices', component: ListIndicesComponent },
  { path: 'indices/:id', component: IndexDetailComponent },
  { path: 'search', component: SearchComponent },
  { path: 'search/:indexId/:hitId', component: SearchDetailComponent },
  { path: 'settings', component: SettingsListComponent },
  { path: '',
    redirectTo: '/indices',
    pathMatch: 'full'
  }
  // { path: '**', component: PageNotFoundComponent }
];

export let rootRoutes = RouterModule.forRoot(
  appRoutes,
  { enableTracing: !environment.production } // <-- debugging purposes only
);

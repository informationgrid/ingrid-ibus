/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import { SearchDetailComponent } from './+search/+search-detail/search-detail.component';
import { SearchComponent } from './+search/search/search.component';
import { IndexDetailComponent } from './+indices/+index-detail/index-detail.component';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { RouterModule, Routes } from '@angular/router';
import { SettingsListComponent } from './+settings/settings-list/settings-list.component';
import { ConnectedIplugsComponent } from './+iplugs/connected-iplugs/connected-iplugs.component';
import { environment } from '../environments/environment';
import {FirstPasswordGuard} from './shared/first-password.guard';
import {ConfigIndexComponent} from './+indices/config-index/config-index.component';

const appRoutes: Routes = [
  {path: 'iplugs', component: ConnectedIplugsComponent, canActivate: [FirstPasswordGuard]},
  {path: 'indices', component: ListIndicesComponent, canActivate: [FirstPasswordGuard]},
  {path: 'indices/:id', component: IndexDetailComponent, canActivate: [FirstPasswordGuard]},
  {path: 'search', component: SearchComponent, canActivate: [FirstPasswordGuard]},
  {path: 'search/:indexId/:hitId', component: SearchDetailComponent, canActivate: [FirstPasswordGuard]},
  {path: 'settings', component: SettingsListComponent},
  {path: 'configIndex', component: ConfigIndexComponent},
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

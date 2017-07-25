import { SearchDetailComponent } from './+search/+search-detail/search-detail.component';
import { SearchComponent } from './+search/search/search.component';
import { IndexDetailComponent } from './+indices/+index-detail/index-detail.component';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { RouterModule, Routes } from '@angular/router';

const appRoutes: Routes = [
  { path: 'indices', component: ListIndicesComponent },
  { path: 'indices/:id', component: IndexDetailComponent },
  { path: 'search', component: SearchComponent },
  { path: 'search/:indexId/:hitId', component: SearchDetailComponent },
  { path: '',
    redirectTo: '/indices',
    pathMatch: 'full'
  }
  // { path: '**', component: PageNotFoundComponent }
];

export default RouterModule.forRoot(
  appRoutes,
  { enableTracing: true } // <-- debugging purposes only
)

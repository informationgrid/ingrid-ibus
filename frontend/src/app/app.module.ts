import { IndexDetailComponent } from './+indices/list-indices/index-detail/index-detail.component';
import { IndicesModule } from './+indices/indices.module';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';

const appRoutes: Routes = [
  { path: 'indices', component: ListIndicesComponent },
  { path: 'indices/:id', component: IndexDetailComponent },
  { path: '',
    redirectTo: '/indices',
    pathMatch: 'full'
  }
  // { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
    IndicesModule
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'de' } // <-- use correct locale for dates
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

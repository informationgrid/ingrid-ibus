import { IndicesModule } from './+indices/indices.module';
import { ListIndicesComponent } from './+indices/list-indices/list-indices.component';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';

const appRoutes: Routes = [
  { path: 'indices', component: ListIndicesComponent },
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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

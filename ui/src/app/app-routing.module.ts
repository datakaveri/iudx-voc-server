import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClassesComponent } from './classes/classes.component';
import { HomeComponent } from './home/home.component';
import { PropertiesComponent } from './properties/properties.component';
import { SchemaDetailsComponent } from './schema-details/schema-details.component';
import { DataModelsComponent } from './data-models/data-models.component';
import { EntitiesComponent } from './entities/entities.component';
import { SearchResultComponent } from './search-result/search-result.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DataModelDomainComponent } from './data-models/data-model-domain/data-model-domain.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
    // pathMatch: 'full'
  },
  {
    path: 'list/classes',
    component: ClassesComponent
  },
  {
    path: 'list/properties',
    component: PropertiesComponent
  },
  {
    path: 'datamodels',
    component: DataModelsComponent
  },
  {
    path: 'entities',
    component: EntitiesComponent
  },
  {
    path: ':schemaName',
    component: SchemaDetailsComponent
  },

  {
    path: 'search/searchTerm',
    component: SearchResultComponent
  },
  {
    path: 'datamodels/:domainName',
    component: DataModelDomainComponent
  },
  {
    path: '404/not-found',
    component: PageNotFoundComponent
  },

  {
    path: '**',
    redirectTo: '404/not-found'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}

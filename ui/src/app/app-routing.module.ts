import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClassesComponent } from './classes/classes.component';
import { HomeComponent } from './home/home.component';
import { PropertiesComponent } from './properties/properties.component';
import { SchemaDetailsComponent } from './schema-details/schema-details.component';
import { DataModelsDomainComponent } from './data-models-domain/data-models-domain.component';
import { EntitiesComponent } from './entities/entities.component';
import { SearchResultComponent } from './search-result/search-result.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DataModelsComponent } from './data-models-domain/data-models/data-models.component';
import { DataDescriptorsComponent } from './data-descriptors/data-descriptors.component';
import { DescriptorDetailsComponent } from './data-descriptors/descriptor-details/descriptor-details.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    // pathMatch: 'full'
  },
  {
    path: 'list/classes',
    component: ClassesComponent,
  },
  {
    path: 'list/properties',
    component: PropertiesComponent,
  },
  {
    path: 'datamodels',
    component: DataModelsDomainComponent,
  },
  {
    path: 'entities',
    component: EntitiesComponent,
  },
  {
    path: 'data-descriptors',
    component: DataDescriptorsComponent,
  },
  {
    path: 'data-descriptors/details',
    component: DescriptorDetailsComponent,
  },
  {
    path: ':schemaName',
    component: SchemaDetailsComponent,
  },

  {
    path: 'search/searchTerm',
    component: SearchResultComponent,
  },
  {
    path: 'datamodels/:domainName',
    component: DataModelsComponent,
  },
  {
    path: '404/not-found',
    component: PageNotFoundComponent,
  },

  {
    path: '**',
    redirectTo: '404/not-found',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

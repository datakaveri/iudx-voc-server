import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-descriptor-details',
  templateUrl: './descriptor-details.component.html',
  styleUrls: ['./descriptor-details.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DescriptorDetailsComponent implements OnInit {
  obj = {
    '@context': [
      'https://voc.iudx.org.in/',
      {
        'qudt-unit': 'http://qudt.org/vocab/unit/',
        unitCode: {
          '@type': '@id',
        },
      },
    ],
    type: ['iudx:EnvAQM'],
    atmosphericPressure: {
      type: ['ValueDescriptor'],
      description: 'Measured Air pressure',
      unitCode: 'qudt-unit:MILLIBAR',
      unitText: 'Milli Bar',
      dataSchema: 'iudx:Number',
    },
    airQualityIndex: {
      type: ['ValueDescriptor'],
      description: 'Overall AQI ',
      unitCode: 'C62',
      unitText: 'dimensionless',
      dataSchema: 'iudx:Number',
    },
    aqiMajorPollutant: {
      type: ['ValueDescriptor'],
      description: 'Major pollutant in the AQI index.',
      dataSchema: 'iudx:Text',
    },
    co: {
      type: ['TimeSeriesAggregation'],
      description:
        'Describes instantaneous and/or aggregated values for carbon monooxide(CO). TimeSeriesAggregations of CO are derived over the last 24 hours',
      avgOverTime: {
        type: ['ValueDescriptor'],
        description: 'Average value of CO for the last 24 hours',
        dataSchema: 'iudx:Number',
        aggregationDuration: 24,
        unitCode: 'X59',
        unitText: 'part per million (ppm)',
      },
      maxOverTime: {
        type: ['ValueDescriptor'],
        description: 'Maximum value of CO for the last 24 hours',
        dataSchema: 'iudx:Number',
        aggregationDuration: 24,
        unitCode: 'X59',
        unitText: 'part per million (ppm)',
      },
      minOverTime: {
        type: ['ValueDescriptor'],
        description: 'Maximum value of CO for the last 24 hours',
        dataSchema: 'iudx:Number',
        aggregationDuration: 24,
        unitCode: 'X59',
        unitText: 'part per million (ppm)',
      },
    },
    pm2p5: {
      type: ['TimeSeriesAggregation'],
      description:
        'Describes instantaneous and/or aggregated values for PM2.5. TimeSeriesAggregations of PM2.5 are derived over the last 24 hours',
      instValue: {
        type: ['ValueDescriptor'],
        description: 'Instantaneous value of pollutant PM2p5.',
        dataSchema: 'iudx:Number',
        unitCode: 'XGQ',
        unitText: 'micro gram per cubic metre (ug/m3)',
        resolution: {
          value: 0.1,
          unitCode: 'XGQ',
        },
      },
      avgOverTime: {
        type: ['ValueDescriptor'],
        description: 'Average value of PM2.5 for the last 24 hours',
        dataSchema: 'iudx:Number',
        aggregationDuration: 24,
        unitCode: 'XGQ',
        unitText: 'micro gram per cubic metre (ug/m3)',
      },
    },
    co2: {
      type: ['TimeSeriesAggregation'],
      description:
        'Describes instantaneous and/or aggregated values for CO2. TimeSeriesAggregations of CO2 are derived over the last 24 hours',
      avgOverTime: {
        type: ['ValueDescriptor'],
        description: 'Average value of CO2 for the last 24 hours',
        dataSchema: 'iudx:Number',
        aggregationDuration: 24,
        unitCode: 'X59',
        unitText: 'part per million (ppm)',
      },
    },
    pm10: {
      type: ['ValueDescriptor'],
      description: 'Instantaneous value of pollutant PM10.',
      dataSchema: 'iudx:Number',
      unitCode: 'XGQ',
      unitText: 'micro gram per cubic metre (ug/m3)',
      resolution: {
        value: 0.5,
        unitCode: 'XGQ',
      },
      measAccuracy: {
        minValue: -10,
        maxValue: 10,
        unitCode: 'qudt-unit:Percent',
        unitText: 'Percent',
      },
    },
  };
  constructor() {}

  ngOnInit(): void {
    console.log(Object.keys(this.obj));
  }
}

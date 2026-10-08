export enum SensorTypeDataTypeEnum {
  REAL = 1,
  BOOLEAN = 2,
  GPS = 3,
  ACCELEROMETER = 4
}

export const SENSOR_DATA_TYPE_OPTIONS = [
  {
    value: SensorTypeDataTypeEnum.REAL,
    label: 'Real'
  },
  {
    value: SensorTypeDataTypeEnum.BOOLEAN,
    label: 'Booleano'
  },
  {
    value: SensorTypeDataTypeEnum.GPS,
    label: 'GPS'
  },
  {
    value: SensorTypeDataTypeEnum.ACCELEROMETER,
    label: 'Acelerômetro'
  }
];

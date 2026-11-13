export interface SensorResumeDTO {
  id_device: number,
  index: number,
  deviceId: String,
  deviceOwnerName: String,
  deviceOwnerEmail: String,
  id_sensortype: number,
  dataType: number;
  sensorTypeDescription: string,
  physicalQuantity: string,
  measureUnit: string,
  summaryPeriod: number | null
}

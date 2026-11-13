import { Component, inject } from '@angular/core';

import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Clipboard, ClipboardModule } from '@angular/cdk/clipboard';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface DeviceCreatedDialogData {
  deviceId: string;
}

@Component({
  selector: 'app-dialog-show-device-id',
  imports: [
    MatTooltipModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    ClipboardModule
  ],
  templateUrl: './dialog-show-device-id.html',
  styleUrl: './dialog-show-device-id.scss',
})
export class DialogShowDeviceId {


  readonly data = inject<DeviceCreatedDialogData>(MAT_DIALOG_DATA);

  private readonly dialogRef =
    inject(MatDialogRef<DialogShowDeviceId>);

  private readonly clipboard = inject(Clipboard);

  copiarDeviceId(): void {
    this.clipboard.copy(this.data.deviceId);
  }

  fechar(): void {
    this.dialogRef.close(true);
  }
}

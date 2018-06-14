import { Component, Input, OnInit } from '@angular/core';
import { IPlugService, PlugDescription } from '../iplug-service.service';

@Component({
  selector: 'iplug-item',
  templateUrl: './iplug-item.component.html',
  styleUrls: ['../../+indices/index-item/index-item.component.scss', './iplug-item.component.scss']
})
export class IPlugItemComponent implements OnInit {

  @Input() data: any;

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {
  }

  activateIPlug(iPlug: PlugDescription) {
    iPlug.activated = !iPlug.activated;
    this.iPlugService.setActive( iPlug.proxyServiceUrl, iPlug.activated ).subscribe(
      null,
      error => this.handleError(error)
    );
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
  }
}

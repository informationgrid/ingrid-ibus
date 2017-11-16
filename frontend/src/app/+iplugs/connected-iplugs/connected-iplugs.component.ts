import { PlugDescription, IPlugService } from '../iplug-service.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-connected-iplugs',
  templateUrl: './connected-iplugs.component.html',
  styleUrls: ['../../+indices/index-item/index-item.component.css', './connected-iplugs.component.css']
})
export class ConnectedIplugsComponent implements OnInit {

  listOfIPlugs: PlugDescription[];

  showInfo = false;

  error = '';

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {
    this.iPlugService.getConnectedIPlugs().subscribe(
      iPlugs => this.listOfIPlugs = iPlugs.filter( p => !p.proxyServiceUrl.startsWith( '__' ) ),
      error => this.handleError( error )
    )
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
    this.error = error;
  }

}

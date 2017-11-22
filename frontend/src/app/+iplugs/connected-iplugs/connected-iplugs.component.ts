import { PlugDescription, IPlugService } from '../iplug-service.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-connected-iplugs',
  templateUrl: './connected-iplugs.component.html',
  styleUrls: ['../../+indices/index-item/index-item.component.css', './connected-iplugs.component.css']
})
export class ConnectedIplugsComponent implements OnInit {

  iplugsLocalIndex: PlugDescription[];
  iplugsCentralIndex: PlugDescription[];

  showInfo = false;

  error = '';

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {
    this.iPlugService.getConnectedIPlugs()
      .subscribe(
      iPlugs => {
        this.iplugsLocalIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && !p.useRemoteElasticsearch);
        this.iplugsCentralIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && p.useRemoteElasticsearch);
      },
      error => this.handleError( error )
    )
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
    this.error = error;
  }

}

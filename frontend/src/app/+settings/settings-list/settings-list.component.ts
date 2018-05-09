import { Component, OnInit } from '@angular/core';
import { IndexService } from '../../+indices/index.service';

@Component({
  selector: 'app-settings-list',
  templateUrl: './settings-list.component.html',
  styleUrls: ['./settings-list.component.scss']
})
export class SettingsListComponent implements OnInit {

  esUrls = ['url1', 'url2'];

  activeIndices: string[];

  constructor(private indexService: IndexService) { }

  ngOnInit() {
    this.indexService.getActiveComponentIds().subscribe(
      ids => this.activeIndices = ids
    );
  }

}

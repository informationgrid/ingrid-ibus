import { Component, Input, OnInit } from '@angular/core';
import { SearchHit } from '../SearchHit';
import { Router } from '@angular/router';

@Component({
  selector: 'hit-item',
  templateUrl: './hit-item.component.html',
  styleUrls: ['./hit-item.component.css']
})
export class HitItemComponent implements OnInit {

  @Input() hit: SearchHit;

  constructor(private router: Router) {
  }

  ngOnInit() {
  }

  showDetailHit(hit: SearchHit) {
    this.router.navigate(['/search/' + hit.esIndex + '/' + hit.hitDetail['0']]);
  }
}

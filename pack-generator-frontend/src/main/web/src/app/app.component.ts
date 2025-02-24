import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
 public welcomePage: boolean = true;
  constructor(
    private router: Router,
  ) {}
  ngOnInit(): void {
    this.checkRoutedUrl();
  }

  packageGenerator(){
    this.welcomePage = false;
    this.router.navigateByUrl('/pack-generator');
  }

  logFiles(){
    this.welcomePage = false;
    this.router.navigateByUrl('/logfile')
  }

  checkRoutedUrl(){
    let currentURL = window.location.href;
    if(currentURL.includes('pack-generator') ||currentURL.includes('logfile')){
      this.welcomePage = false;
    }

  }
  }

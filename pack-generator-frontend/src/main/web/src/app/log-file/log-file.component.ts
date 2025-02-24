import { Component, OnInit } from '@angular/core';
import { ApiService } from '../service/api.service';

@Component({
  selector: 'app-log-file',
  templateUrl: './log-file.component.html',
  styleUrls: ['./log-file.component.css']
})
export class LogFileComponent implements OnInit{
  List: Object;
  log: string;
  Home: boolean;

  constructor(private apiService: ApiService) { }
  ngOnInit(): void {
    this.getLogFiles();
  }

  getLogFiles(){
    this.apiService.fetchLogFiles().subscribe({
      next: (response) => {
        this.List = response;
        console.log('log files found',response);
      },
      error: (error) => {
        console.error('log files not found', error);
      }
    });
  }


  downloadLogFile(fileName:any){
    this.apiService.downloadLogFile(fileName).subscribe(
      (response: Blob) => {
        // Create a new URL for the Blob
        const blob = new Blob([response]);
        const url = window.URL.createObjectURL(blob);

        // Create a temporary anchor element to trigger the download
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName; 
        a.click(); // Programmatically click the anchor to trigger the download

        // Clean up the URL object after download
        window.URL.revokeObjectURL(url);
        console.log('download successful', response);
      },
      (error) => {
                  console.log('Error in downloading file');
      }
    );
  }

  HomePage(){
    this.Home = true;
   }
}

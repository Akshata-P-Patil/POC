import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/service/api.service';

@Component({
  selector: 'app-packgenerator',
  templateUrl: './packgenerator.component.html',
  styleUrls: ['./packgenerator.component.css']
})
export class PackgeneratorComponent {

 downloaded = false ; // Boolean to control whether the text is visible
  swuploadSuccess: boolean;
  swuploadAttempted: boolean;
  s19uploadAttempted: boolean;
  s19uploadSuccess: boolean;
  downloadSuccess: boolean;
  downloadMessage: string;
  downloadAttempted: boolean;
  generateSuccess: boolean;
  ZipAttempted: boolean;
  zipMessage: string;
  Home: boolean;

  constructor(private apiService: ApiService) {}

  download() {
    console.log("clicked");
    this.downloaded = !this.downloaded;
  }

  uploadSwFileMessage: any;
  uploadS19FileMessage: any;


  // Method to handle file selection
  onSwFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files) {
      const file = input.files[0];
      const formData = new FormData();
      formData.append('file', file, file.name);
      if(file.name.includes(".swu")){
        this.apiService.uploadSwFile(formData).subscribe({
          next: (response) => {
            this.swuploadAttempted = true;
            this.swuploadSuccess = true;
            this.uploadSwFileMessage = response.body.message;
            console.log('Upload successful', response);
          },
          error: (error) => {
            this.swuploadAttempted = true;
            this.swuploadSuccess = false;
            this.uploadSwFileMessage = "Error in uploading file";
            console.error('Error uploading file', error);
          }
        });
      }
      else{
        this.swuploadAttempted = true;
        this.swuploadSuccess = false;
        this.uploadSwFileMessage = "Error in uploading file";
      }
      }
      else{
        alert('Please select a file to upload.');
      }
    }
  
  onS19FileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files) {
      const file = input.files[0];
      const formData = new FormData();
      formData.append('file', file, file.name);
      if(file.name.includes(".s19")){
        this.apiService.uploadS19File(formData).subscribe({
          next: (response) => {
            this.s19uploadAttempted = true;
            this.s19uploadSuccess = true;
            this.uploadS19FileMessage = response.body.message;
            console.log('Upload successful', response);
          },
          error: (error) => {
            this.s19uploadAttempted = true;
            this.s19uploadSuccess = false;
            this.uploadS19FileMessage =  "Error in uploading file";
            console.error('Error uploading file', error);
          }
        });
      }
      else{
        this.s19uploadAttempted = true;
        this.s19uploadSuccess = false;
        this.uploadS19FileMessage = "Error in uploading file";
      }
      }
      else{
        alert('Please select a file to upload.');
      }
  }

  downloadFile() {
    this.apiService.downloadFile().subscribe(
      (response: Blob) => {
        // Create a new URL for the Blob
        const blob = new Blob([response]);
        const url = window.URL.createObjectURL(blob);

        // Create a temporary anchor element to trigger the download
        const a = document.createElement('a');
        a.href = url;
        a.download = 'uploadFile.swu'; 
        a.click(); // Programmatically click the anchor to trigger the download

        // Clean up the URL object after download
        window.URL.revokeObjectURL(url);
        this.downloadSuccess = true;
        this.downloadAttempted = true;
        this.downloadMessage = "downloaded successfully"
        console.log('download successful', response);
      },
      (error) => {
        this.downloadSuccess = false;
                  this.downloadAttempted = true;
                  this.downloadMessage =  "Error in downloading, file not found";
                  console.log('Error in downloading file');
      }
    );
  }


      generateZipFile() {
        this.apiService.generateZipFile().subscribe({
          next: (response) => {
           this.generateSuccess = true;
           this.ZipAttempted = true;
           this.zipMessage = " Zip generated successfully"
            console.log('zip generated successful', response);
          },
          error: (error) => {
            this.generateSuccess = false;
            this.ZipAttempted = true;
            this.zipMessage =  "Error in generating zip file";
            console.error('Error in generating zip file', error);
          }
        });
      }

      HomePage(){
       this.Home = true;
      }
}

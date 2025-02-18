import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent{
  downloaded = false ; // Boolean to control whether the text is visible
  uploadError: boolean;

  download() {
    console.log("clicked");
    this.downloaded = !this.downloaded;
  }

  selectedSwFileName: string | null = null;
  selectedS19FileName: string | null = null;


  // Method to handle file selection
  onSwFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files) {
      const file = input.files[0];
      if (file) {
        this.selectedSwFileName = file.name;

        // handle the file uploaded here

        console.log(file); // For testing, logging the selected file
      }
      else{
        this.uploadError = true;
      }
    }
  }


  // Method to handle file selection
  onS19FileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files) {
      const file = input.files[0];
      if (file) {
        this.selectedS19FileName = file.name;

        // handle the file uploaded here

        console.log(file); // For testing, logging the selected file
      }
    }
  }
}

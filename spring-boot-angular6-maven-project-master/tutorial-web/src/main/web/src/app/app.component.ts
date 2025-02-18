import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent{
  downloaded = false ; // Boolean to control whether the text is visible

  download() {
    console.log("clicked");
    this.downloaded = !this.downloaded;
  }

  selectedFileName: string | null = null;

  // Method to handle file selection
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files) {
      const file = input.files[0];
      if (file) {
        this.selectedFileName = file.name;

        // handle the file uploaded here

        console.log(file); // For testing, logging the selected file
      }
    }
  }
}

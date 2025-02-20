import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private swFileApiUrl = 'http://localhost:8080/api/uploadSwuData';
  private s19FileApiUrl = 'http://localhost:8080/api/uploadS19Data';
  private downloadApiUrl = 'http://localhost:8080/api/download';
  private zipApiUrl = 'http://localhost:8080/api/generateZip';


  constructor(private http: HttpClient) {}


  // This method uploads sw file and returns an observable of the response
  uploadSwFile(formData: FormData): Observable<HttpResponse<any>> {
    return this.http.post<HttpResponse<any>>(this.swFileApiUrl, formData, {
      observe: 'response'
    });
  }

    // This method uploads s19 file and returns an observable of the response
    uploadS19File(formData: FormData): Observable<HttpResponse<any>> {
      return this.http.post<HttpResponse<any>>(this.s19FileApiUrl, formData, {
        observe: 'response'
      });
    }

    //This method downloads file (.swu file)
    downloadFile():Observable<HttpResponse<any>> {
      return this.http.get<HttpResponse<any>>(this.downloadApiUrl, {
        observe: 'response'
      });
    }

    //This method generates zip file
    generateZipFile(): Observable<HttpResponse<Blob>> {
      return this.http.get<Blob>(this.zipApiUrl, {
        observe: 'response',
        responseType: 'blob' as 'json' // Specify the response type as 'blob'
      });
    }
}



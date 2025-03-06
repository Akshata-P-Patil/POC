import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private swFileApiUrl = 'http://localhost:8080/pack-generator/uploadSwuFile';
  private s19FileApiUrl = 'http://localhost:8080/pack-generator/uploadS19File';
  private downloadApiUrl = 'http://localhost:8080/pack-generator/download';
  private zipApiUrl = 'http://localhost:8080/pack-generator/generateZip';
  private LogFilesUrl = 'http://localhost:8080/api/getList';
  private downloadLogUrl ='http://localhost:8080/api/downloadAuditLog?fileName=';


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
    downloadFile(): Observable<Blob> {
      return this.http.get(this.downloadApiUrl, {
        responseType: 'blob'  // This ensures we get the file as a Blob
      });
    }
  

    //This method generates zip file
    generateZipFile(): Observable<HttpResponse<Blob>> {
      return this.http.get<Blob>(this.zipApiUrl, {
        observe: 'response',
        responseType: 'blob' as 'json' // Specify the response type as 'blob'
      });
    }

    //This method fetches list of log files 
    fetchLogFiles():Observable<Object>{
      return this.http.get<any>(this.LogFilesUrl);
    }

    //download file based on log file name:
    downloadLogFile(fileName: String): Observable<Blob> {
      return this.http.get(this.downloadLogUrl + fileName, {
        responseType: 'blob'  // This ensures we get the file as a Blob
      });
    }
  
}



# AndroidGoogleFit    
 ## Note when test this application 
 - Filter Logcat by GoogleFitDemo TAG then check the result in Logcat  
  
  
## Experience  
### Inserted data vs original data    
 #### PackageName   
Original      
```  
return null
```  
Inserted  
``` 
return the package name of inserted application. If user don't specific the packageName when create data source, user will receive error **INCONSISTENT_PACKAGE_NAME**(5015)

If we insert step from GoogleFit app, packageName="com.google.android.apps.fitness"
```  
### Stream name  
Original  
``` 
return "Step Counter"  
```  
Inserted  
```  
return the stream name when we have configed datasource (it can be "Step Counter")  

If we insert step from GoogleFit app, streamName="user_input"
```  
### Stream identifier  
Original   
```  
example return  
raw:com.google.step_count.cumulative:Sony:SOL26:55832610:Step Counter 
```  
Inserted   
```  
example return  
raw:com.google.step_count.delta:toong.vn.androidgooglefit:Step Counter  

If we insert step from GoogleFit app
raw:com.google.step_count.delta:com.google.android.apps.fitness:user_input
```
package my.maze.malaysia

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

enum class Appearance { SYSTEM, DAY, NIGHT }
data class MazeState(
 val query:String="",val origin:Place=landmarks[0],val destination:Place=landmarks[1],val results:List<Place> = landmarks,
 val searchBusy:Boolean=false,val routeBusy:Boolean=false,val routes:List<RoadRoute> = emptyList(),val selectedRoute:Int=0,
 val message:String?=null,val fetchedAt:Long?=null,val provider:String="",val dataAt:Long?=null,val sample:Boolean=BuildConfig.IS_LAB,
 val savedPreview:Boolean=false,val savedTrips:List<SavedTrip> = emptyList(),val saving:Boolean=false,val appearance:Appearance=Appearance.SYSTEM,
 val online:Boolean=true,val offlineArea:OfflineAreaState=OfflineAreaState(),val servicesConfigured:Boolean=false
)
private fun restorePlace(saved:SavedStateHandle,key:String,fallback:Place):Place {
 val id=saved.get<String>(key) ?: return fallback;landmarks.find{it.id==id}?.let{return it};if(id=="current")return fallback
 return runCatching{val point=GeoPoint(saved.get<Double>(key+"-lat")!!,saved.get<Double>(key+"-lon")!!);require(inMalaysiaRegion(point));Place(id,saved.get<String>(key+"-name")!!,saved.get<String>(key+"-area") ?: "Malaysia",point)}.getOrDefault(fallback)
}
class MazeViewModel(application:Application,private val saved:SavedStateHandle):AndroidViewModel(application) {
 private val repository=MazeRepository();private val store=TripStore(application);private val preferences=application.getSharedPreferences("maze-settings",Context.MODE_PRIVATE)
 var state by mutableStateOf(MazeState(query=saved["query"] ?: "",origin=restorePlace(saved,"origin",landmarks[0]),destination=restorePlace(saved,"destination",landmarks[1]),appearance=runCatching{Appearance.valueOf(preferences.getString("appearance","SYSTEM")!!)}.getOrDefault(Appearance.SYSTEM),servicesConfigured=repository.configured));private set
 private var searchJob:Job?=null;private var routeJob:Job?=null;private var searchGeneration=0;private var routeGeneration=0
 private val offline=MazeOffline(application){area->state=state.copy(offlineArea=area)}
 private val connectivity=application.getSystemService(ConnectivityManager::class.java)
 private val networkCallback=object:ConnectivityManager.NetworkCallback(){override fun onCapabilitiesChanged(network:Network,c:NetworkCapabilities){viewModelScope.launch{state=state.copy(online=c.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))}};override fun onLost(network:Network){viewModelScope.launch{state=state.copy(online=false)}}}
 init {connectivity.registerDefaultNetworkCallback(networkCallback);val c=connectivity.getNetworkCapabilities(connectivity.activeNetwork);state=state.copy(online=c?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)==true);viewModelScope.launch{try{val trips=withContext(Dispatchers.IO){store.read()};state=state.copy(savedTrips=trips)}catch(e:Exception){state=state.copy(message="Saved trips could not be opened. You can clear them from Saved trips.")}}}
 fun query(value:String){searchGeneration++;searchJob?.cancel();val q=value.take(160);state=state.copy(query=q,results=localSearch(q),searchBusy=false,message=null);saved["query"]=q}
 fun onlineSearch(){val q=state.query.trim();if(q.length<2){message("Enter at least two characters.");return};val token=++searchGeneration;searchJob?.cancel();state=state.copy(searchBusy=true,message=null);searchJob=viewModelScope.launch{try{val found=repository.search(q);if(token==searchGeneration)state=state.copy(results=found,searchBusy=false,message=if(found.isEmpty())"No matching places. Try a full address or choose a saved landmark." else null)}catch(e:CancellationException){throw e}catch(e:Exception){if(token==searchGeneration)state=state.copy(searchBusy=false,message=recoveryMessage(e))}}}
 fun select(place:Place,origin:Boolean=false){routeGeneration++;routeJob?.cancel();state=if(origin)state.copy(origin=place,routes=emptyList(),routeBusy=false,fetchedAt=null,message=null,savedPreview=false)else state.copy(destination=place,routes=emptyList(),routeBusy=false,fetchedAt=null,message=null,savedPreview=false);val key=if(origin)"origin" else "destination";saved[key]=place.id;if(place.id!="current"){saved[key+"-name"]=place.name;saved[key+"-area"]=place.area;saved[key+"-lat"]=place.point.lat;saved[key+"-lon"]=place.point.lon}else for(suffix in listOf("-name","-area","-lat","-lon"))saved.remove<Any>(key+suffix)}
 fun findRoutes(){if(state.routeBusy)return;val from=state.origin;val to=state.destination;val token=++routeGeneration;state=state.copy(routeBusy=true,message=null)
  routeJob=viewModelScope.launch{try{val reply=repository.routes(from,to);if(token==routeGeneration)state=state.copy(routeBusy=false,routes=reply.routes,selectedRoute=0,fetchedAt=System.currentTimeMillis(),provider=reply.provider,dataAt=reply.dataAt,sample=reply.sample,savedPreview=false)}catch(e:CancellationException){throw e}catch(e:Exception){if(token==routeGeneration){val trip=state.savedTrips.firstOrNull{it.id==tripKey(from,to)};if(state.routes.isEmpty()&&trip!=null)openTrip(trip);state=state.copy(routeBusy=false,savedPreview=state.routes.isNotEmpty(),message=recoveryMessage(e,state.routes.isNotEmpty()))}}}
 }
 fun chooseRoute(index:Int){if(index in state.routes.indices)state=state.copy(selectedRoute=index)}
 fun message(value:String){state=state.copy(message=value)}
 fun appearance(value:Appearance){preferences.edit().putString("appearance",value.name).apply();state=state.copy(appearance=value)}
 fun saveTrip(){val route=state.routes.getOrNull(state.selectedRoute) ?: return;if(state.saving)return;val trip=SavedTrip(tripKey(state.origin,state.destination),state.origin,state.destination,route,System.currentTimeMillis(),state.provider,state.dataAt,state.sample);val trips=(listOf(trip)+state.savedTrips.filter{it.id!=trip.id}).take(4);state=state.copy(saving=true);viewModelScope.launch{try{withContext(Dispatchers.IO){store.write(trips)};state=state.copy(savedTrips=trips,saving=false,message="Trip saved on this device. Directions remain available offline.")}catch(e:Exception){state=state.copy(saving=false,message="Trip could not be saved. Check free storage and try again.")}}}
 fun openTrip(trip:SavedTrip){routeGeneration++;routeJob?.cancel();state=state.copy(origin=trip.origin,destination=trip.destination,routes=listOf(trip.route),selectedRoute=0,fetchedAt=trip.savedAt,provider=trip.provider,dataAt=trip.dataAt,sample=trip.sample,savedPreview=true,routeBusy=false,message="Saved preview. Road conditions may have changed.")}
 fun clearTrips(){viewModelScope.launch{try{withContext(Dispatchers.IO){store.clear()};state=state.copy(savedTrips=emptyList(),message="Saved trips removed from this device.")}catch(e:Exception){state=state.copy(message="Could not remove saved trips. Try again.")}}}
 fun downloadArea(night:Boolean){if(!state.online){message("Connect to the internet before saving a map area.");return};offline.download(state.destination,night)}
 fun removeArea(){offline.remove()}
 override fun onCleared(){super.onCleared();connectivity.unregisterNetworkCallback(networkCallback);offline.close()}
}

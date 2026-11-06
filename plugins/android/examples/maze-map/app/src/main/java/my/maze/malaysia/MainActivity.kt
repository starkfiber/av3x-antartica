package my.maze.malaysia

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.Location
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.maplibre.android.module.http.HttpRequestUtil
import org.maplibre.android.MapLibre
import okhttp3.OkHttpClient

private val Forest=Color(0xFF0B715F)
private val Ink=Color(0xFF103C35)
private val Warm=Color(0xFFF7FAF6)
class MainActivity:ComponentActivity() {
    private var model:MazeViewModel?=null
    private var locationCancellation:CancellationSignal?=null
    private var legacyListener:android.location.LocationListener?=null
    private val handler=Handler(Looper.getMainLooper())
    private val locationPermissions=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        if(grants.values.any{it})findLocation() else model?.message("Location is optional. Choose a start point instead.")
    }
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);enableEdgeToEdge()
        MapLibre.getInstance(this)
        val dispatcher=okhttp3.Dispatcher().apply { maxRequests=4;maxRequestsPerHost=4 }
        HttpRequestUtil.setOkHttpClient(OkHttpClient.Builder().dispatcher(dispatcher).addNetworkInterceptor { chain -> chain.proceed(chain.request().newBuilder().header("User-Agent","MazeMalaysiaWay/0.2 (Android map beta)").build()) }.build())
        setContent {
            val vm:MazeViewModel=viewModel();model=vm
            MazeTheme(vm.state.appearance) { night -> MazeScreen(vm,night) { locationPermissions.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)) } }
        }
    }

    private fun findLocation() {
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)return
        val manager=getSystemService(LocationManager::class.java)
        val provider=if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
        model?.message("Finding a current location…")
        val accept:(Location?)->Unit={ location ->
            locationCancellation=null
            if(location==null)model?.message("No location fix. Choose a start point instead.")
            else runCatching { GeoPoint(location.latitude,location.longitude) }.onSuccess { point ->
                if(!inMalaysiaRegion(point))model?.message("This fix is outside the Malaysia map region. Choose a start point.")
                else model?.select(Place("current","Current location","One foreground fix",point),origin=true)
            }.onFailure { model?.message("The location fix could not be used.") }
        }
        try {
            locationCancellation?.cancel()
            if(android.os.Build.VERSION.SDK_INT>=30) {
                val cancellation=CancellationSignal();locationCancellation=cancellation
                manager.getCurrentLocation(provider,cancellation,mainExecutor,accept)
                handler.postDelayed({if(locationCancellation===cancellation){cancellation.cancel();locationCancellation=null;model?.message("Location timed out. Choose a start point instead.")}},15000)
            } else {
                val listener=object:android.location.LocationListener {
                    override fun onLocationChanged(location:Location){manager.removeUpdates(this);legacyListener=null;accept(location)}
                    override fun onProviderEnabled(provider:String){}
                    override fun onProviderDisabled(provider:String){}
                    @Deprecated("Deprecated by Android") override fun onStatusChanged(provider:String?,status:Int,extras:Bundle?){}
                }
                legacyListener=listener;manager.requestSingleUpdate(provider,listener,Looper.getMainLooper())
                handler.postDelayed({if(legacyListener===listener){manager.removeUpdates(listener);legacyListener=null;accept(null)}},15000)
            }
        } catch(e:Exception){model?.message("Location unavailable. Choose a start point instead.")}
    }
    override fun onStop(){super.onStop();locationCancellation?.cancel();locationCancellation=null;legacyListener?.let{getSystemService(LocationManager::class.java).removeUpdates(it)};legacyListener=null;handler.removeCallbacksAndMessages(null)}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun MazeScreen(model:MazeViewModel,night:Boolean,onLocation:()->Unit) {
 val s=model.state;val colors=MaterialTheme.colorScheme
 var sheet by remember { mutableStateOf("") };var expanded by remember { mutableStateOf(true) };var explain by remember { mutableStateOf(false) }
 var mapReady by remember { mutableStateOf(false) };var header by remember{mutableStateOf(0)};var panel by remember{mutableStateOf(0)};var panelWidth by remember{mutableStateOf(0)}
 val window=LocalWindowInfo.current.containerSize;val wide=window.width>window.height;val maxPanel=with(LocalDensity.current){window.height.toDp()}*(if(wide).78f else .50f)
 val route=s.routes.getOrNull(s.selectedRoute);val uri=LocalUriHandler.current
 val mapStyle=if(!s.online&&s.offlineArea.ready)s.offlineArea.style ?: DAY_STYLE else if(night)NIGHT_STYLE else DAY_STYLE
 Box(Modifier.fillMaxSize().background(colors.background)) {
  MazeMap(s,Modifier.fillMaxSize(),topInset=header+20,bottomInset=if(wide)40 else panel+20,rightInset=if(wide)panelWidth+20 else 40,styleUri=mapStyle){mapReady=it}
  Surface(Modifier.align(Alignment.TopStart).onSizeChanged{header=it.height}.then(if(wide)Modifier.width(330.dp)else Modifier.fillMaxWidth()).statusBarsPadding().padding(12.dp),shape=RoundedCornerShape(24.dp),shadowElevation=5.dp) {
   Column(Modifier.padding(horizontal=16.dp,vertical=8.dp)) {
    Row(verticalAlignment=Alignment.CenterVertically) { Image(painterResource(R.drawable.maze_icon),"Maze",Modifier.size(32.dp));Text("maze",Modifier.weight(1f).padding(start=10.dp),fontSize=25.sp,fontWeight=FontWeight.Bold);TextButton(onClick={sheet="saved"}){Text("Saved")};TextButton(onClick={sheet="settings"}){Text("More")} }
    TextButton(onClick={sheet="search"},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) { Column(Modifier.weight(1f)) {Text("Where to?",style=MaterialTheme.typography.labelMedium);Text(s.destination.name,maxLines=1,overflow=TextOverflow.Ellipsis,fontWeight=FontWeight.SemiBold)};Text("Search") }
    if(BuildConfig.IS_LAB)Text("LAB · sample search & recorded routes",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
    if(!s.online)Text(if(s.offlineArea.ready)"Offline · saved map style" else "Offline · map coverage may be missing",style=MaterialTheme.typography.labelSmall)
   }
  }
  Surface(Modifier.align(if(wide)Alignment.BottomEnd else Alignment.BottomCenter).then(if(wide)Modifier.width(370.dp)else Modifier.fillMaxWidth()).onSizeChanged{panel=it.height;panelWidth=it.width},shape=RoundedCornerShape(topStart=28.dp,topEnd=28.dp),shadowElevation=8.dp) {
   Column(Modifier.navigationBarsPadding().heightIn(max=maxPanel).verticalScroll(rememberScrollState()).padding(horizontal=20.dp,vertical=8.dp)) {
    Row(verticalAlignment=Alignment.CenterVertically) { Column(Modifier.weight(1f)){Text(if(route==null)"YOUR JOURNEY" else if(s.savedPreview)"SAVED PREVIEW" else if(s.sample)"RECORDED SAMPLE" else "ROAD ESTIMATE",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant);Text(if(route==null)s.origin.name else "${routeMinutes(route.seconds)} min · ${routeDistanceLabel(route.meters)}",fontSize=22.sp,fontWeight=FontWeight.Bold)};TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Less detail" else "Trip details")} }
    if(s.routeBusy)LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical=6.dp))
    if(s.message!=null)Surface(color=colors.secondaryContainer,shape=RoundedCornerShape(12.dp),modifier=Modifier.padding(vertical=6.dp).semantics{liveRegion=LiveRegionMode.Polite}){Text(s.message,Modifier.padding(12.dp),style=MaterialTheme.typography.bodySmall)}
    if(expanded) {
     Row {TextButton(onClick={sheet="origin"}){Text("Change start")};TextButton(onClick={explain=true}){Text("Locate me")};TextButton(onClick={sheet="offline"}){Text("Map area")} }
     if(route!=null) {
      Text("${s.origin.name} to ${s.destination.name}",style=MaterialTheme.typography.bodyMedium)
      Text("${s.provider} · ${timeLabel(s.dataAt ?: s.fetchedAt)}",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
      Text("No live traffic · planning preview",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
      if(s.routes.size>1)LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(s.routes.indices.toList()){i->FilterChip(i==s.selectedRoute,{model.chooseRoute(i)},label={Text("Route ${i+1} · ${routeMinutes(s.routes[i].seconds)} min")})}}
      Row {TextButton(onClick=model::saveTrip,enabled=!s.saving){Text(if(s.saving)"Saving…" else "Save trip")};TextButton(onClick=model::findRoutes,enabled=!s.routeBusy){Text("Refresh route")} }
     } else Text("A clearer way there. Choose your start and destination.",style=MaterialTheme.typography.bodyMedium)
    }
    Button(onClick={if(route==null)model.findRoutes()else sheet="directions"},enabled=!s.routeBusy,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).padding(vertical=4.dp),shape=RoundedCornerShape(16.dp)){Text(if(route!=null)"View road directions" else if(s.routeBusy)"Finding routes…" else "Find road routes")}
    Text(if(mapReady)"© OpenStreetMap contributors · © OpenMapTiles" else "Loading basemap · saved directions remain available",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
   }
  }
 }
 if(sheet.isNotEmpty())ModalBottomSheet(onDismissRequest={sheet=""},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
  Column(Modifier.fillMaxWidth().heightIn(max=with(LocalDensity.current){window.height.toDp()}*.85f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal=22.dp).padding(bottom=28.dp)) {
   when(sheet) {
    "search"->{Text("Find your place",style=MaterialTheme.typography.headlineSmall);OutlinedTextField(s.query,model::query,label={Text("Search Malaysia")},singleLine=true,modifier=Modifier.fillMaxWidth().padding(vertical=12.dp));Text(if(BuildConfig.IS_LAB)"Lab catalogue only. Local landmarks also work without search services." else "Online search sends this query to your configured service.",style=MaterialTheme.typography.bodySmall);TextButton(onClick=model::onlineSearch,enabled=!s.searchBusy&&s.query.trim().length>=2){Text(if(s.searchBusy)"Searching…" else "Search Malaysia online")};s.results.forEach{p->ListItem(headlineContent={Text(p.name)},supportingContent={Text(p.area)},modifier=Modifier.clickable{model.select(p);sheet=""})};s.message?.let{Text(it,Modifier.semantics{liveRegion=LiveRegionMode.Polite})}}
    "origin"->{Text("Choose your start",style=MaterialTheme.typography.headlineSmall);TextButton(onClick={sheet="";explain=true}){Text("Use current location")};landmarks.forEach{p->ListItem(headlineContent={Text(p.name)},supportingContent={Text(p.area)},modifier=Modifier.clickable{model.select(p,true);sheet=""})}}
    "directions"->{Text("Road directions",style=MaterialTheme.typography.headlineSmall);Text("Planning preview · not active navigation. No live traffic.",Modifier.padding(vertical=12.dp));route?.steps?.forEach{step->ListItem(headlineContent={Text(step.text)},supportingContent={Text(routeDistanceLabel(step.meters))})}}
    "saved"->{Text("Saved trips",style=MaterialTheme.typography.headlineSmall);Text("Up to four trips, stored only on this device. Saved roads and estimates may be out of date.",Modifier.padding(vertical=12.dp));if(s.savedTrips.isEmpty())Text("No saved trips yet. Find a route, then choose Save trip.");s.savedTrips.forEach{trip->ListItem(headlineContent={Text(trip.destination.name)},supportingContent={Text("From ${trip.origin.name} · saved ${timeLabel(trip.savedAt)}"+(if(trip.sample)" · sample" else ""))},modifier=Modifier.clickable{model.openTrip(trip);sheet=""})};TextButton(onClick=model::clearTrips){Text("Clear saved trips")}}
    "offline"->{Text("Save a small map area",style=MaterialTheme.typography.headlineSmall);Text("Around ${s.destination.name}: about 10 km across, zoom levels 11–14. One Day or Night area. Directions must be saved separately. Coverage outside the area is not available offline.",Modifier.padding(vertical=12.dp));Text(s.offlineArea.label,Modifier.semantics{liveRegion=LiveRegionMode.Polite});Text("${s.offlineArea.bytes/1000} kB stored",style=MaterialTheme.typography.bodySmall);if(s.offlineArea.busy)LinearProgressIndicator(Modifier.fillMaxWidth());Button(onClick={model.downloadArea(night)},enabled=!s.offlineArea.busy&&!s.offlineArea.ready){Text("Download map area")};TextButton(onClick=model::removeArea){Text("Remove saved map")};Text("Download stops after the observed 50 MB / 3,000 tile budget; up to four requests at once. Network failures can interrupt it.",style=MaterialTheme.typography.bodySmall)}
    "settings"->{Text("Make yourself at home",style=MaterialTheme.typography.headlineSmall);Text("Appearance",Modifier.padding(top=16.dp));Appearance.entries.forEach{mode->Row(Modifier.fillMaxWidth().heightIn(min=48.dp).selectable(selected=s.appearance==mode,role=Role.RadioButton,onClick={model.appearance(mode)}),verticalAlignment=Alignment.CenterVertically){RadioButton(s.appearance==mode,null);Text(mode.name.lowercase().replaceFirstChar{it.uppercase()})}};Text(if(BuildConfig.IS_LAB)"Lab services: controlled local fixtures. These are recorded routes, not live road conditions." else if(s.servicesConfigured)"Search and routing: configured HTTPS gateway." else "Search and routing are not configured. Local landmarks and saved trips remain available.",Modifier.padding(vertical=12.dp));Text("MapLibre + OpenFreeMap basemap. No advertising SDK, accounts or background location. Map services receive the viewed area. A requested route sends both chosen coordinates. Saving a trip keeps its coordinates on this device. No service is guaranteed never to fail.");TextButton(onClick={runCatching{uri.openUri("https://www.openstreetmap.org/copyright")}.onFailure{model.message("Map credits: openstreetmap.org/copyright")}}){Text("Map data credits")}}
   }
   TextButton(onClick={sheet=""},modifier=Modifier.heightIn(min=48.dp)){Text("Back to map")}
  }
 }
 if(explain)AlertDialog(onDismissRequest={explain=false},title={Text("Your location, your choice")},text={Text("Use one foreground fix as the start. Coordinates are sent only when you request a route. Saving that trip stores its coordinates on this device. No background location or history upload.")},confirmButton={TextButton(onClick={explain=false;onLocation()}){Text("Continue")}},dismissButton={TextButton(onClick={explain=false}){Text("Choose manually")}})
}

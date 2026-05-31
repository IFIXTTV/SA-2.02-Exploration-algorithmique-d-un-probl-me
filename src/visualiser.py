###############################################
## Module de visualisation de trajets au moyen
## de la bibliothèque Folium
## Source: https://medium.com/data-science/visualizing-routes-on-interactive-maps-with-python-part-1-44f8d25d0761
###############################################
from typing import Tuple, List, Dict

import os
import pickle
import pandas as pd
import folium
import webbrowser

pd.set_option('display.precision', 4)

## Configuration (KPI)
TAG_ROUTE_NAME = "Carte de "
TAG_NUMBER_STOPS = "Nombre d'arrêts"
TAG_TOTAL_DISTANCE = "Distance totale (vol d'oiseau)"
_SPACE_HTML = "&nbsp"  
STYLE_TITLE = (
    "position:absolute;z-index:100000;left:5vw;color:black;"
    "font-size:30px;text-shadow:-1px 0 white, 0 1px white, 0 1px white"
)
STYLE_SUMMARY = (
    "position:absolute;z-index:100000;font-size:20px;"
    "right:0;bottom:0;color:black;"
    "text-shadow:-1px 0 white, 0 1px white, 0 1px white"
)

# Distance computation
from geopy.distance import geodesic
_Location = Tuple[float, float]

def ellipsoidal_distance(point1: _Location, point2: _Location) -> float:
    """Calculate ellipsoidal distance (in meters) between point1 and 
    point2 where each point is represented as a tuple (lat, lon)"""
    return geodesic(point1, point2).meters

def _make_route_segments_df(df_route: pd.DataFrame) -> pd.DataFrame:
    """Given a dataframe whose rows are ordered stops in a route, 
    and where the index has integers representing the visit order of those
    stops, return a dataframe having new columns with the information of 
    each stop's next site"""
    df_route_segments = df_route.join(
        df_route.shift(-1),  # map each stop to its next
        rsuffix='_next').dropna()
    
    df_route_segments['distance_seg'] = df_route_segments.apply(
        lambda stop: ellipsoidal_distance(
            (stop.latitude, stop.longitude), 
            (stop.latitude_next, stop.longitude_next)
        ), axis=1
    )
    return df_route_segments

def _get_text_for_title(df_route_segments):
    """Given a dataframe representing a route, where the column index has 
    the name of the route, returns an HTML string with a nice display of 
    this name"""
    name = df_route_segments.columns.name
    name = name.capitalize() if name else ''
    _html_text_title = f"<b>{TAG_ROUTE_NAME}</b>: {name}"
    html_title = f'<h3 style="{STYLE_TITLE}">{_html_text_title}</h3>'
    return html_title

def _get_kpis_to_display_on_map(df_route_segments):
    """Given a dataframe representing a route, and having columns 'site' 
    and 'distance_seg', returns an HTML string with a nice display of 
    the number of sites and the total distance of the route"""
    n_stops = df_route_segments['site'].size
    route_distance = df_route_segments['distance_seg'].sum().round(0)
    _html_text_summary = f"""
    <b>{TAG_NUMBER_STOPS}</b> <b>{TAG_TOTAL_DISTANCE}</b>
    <br>
    {16 * _SPACE_HTML} {n_stops} {16 * _SPACE_HTML} {route_distance:.0f} m
    """
    html_summary = f'<h2 style="{STYLE_SUMMARY}">{_html_text_summary}</h2>'
    return html_summary

def _get_lines(gtfs_dir):
    """Given a directory containing files in GTFS format,
    extract informations about lines and stops,
    returns a dictionnary of folium's FeatureGroup objects.
    """
    feature_groups = {}
    
    if os.path.exists(os.path.join(os.getcwd(), 'network.pkl')):
        with open(os.path.join(os.getcwd(), 'network.pkl'), 'rb') as infile:
            feature_groups = pickle.load(infile)
    else:
        df_lines  = pd.read_csv(os.path.join(gtfs_dir, 'routes.txt'))    
        df_coords = pd.read_csv(os.path.join(gtfs_dir, 'shapes.txt'))
        df_trips  = pd.read_csv(os.path.join(gtfs_dir, 'trips.txt'))
        df_stops  = pd.read_csv(os.path.join(gtfs_dir, 'stop_times.txt'))
        df_snames = pd.read_csv(os.path.join(gtfs_dir, 'stops.txt'))
            
        for l_index,l_row in df_lines.iterrows():
            line_str  = l_row['route_short_name'] + ' ' + l_row['route_long_name']
            line_color= "#"+l_row['route_color']
            feature_groups[line_str] = folium.FeatureGroup(name='line_str')
            points = []
            t_row = df_trips.loc[(df_trips['route_id']==l_row['route_id']) & df_trips['shape_id'].notnull()]
            if len(t_row) > 0:
                trip = t_row.iloc[0]
                df_OK = df_coords.loc[(df_coords['shape_id']==trip['shape_id'])]
                for sh_index,sh_row in df_OK.iterrows():
                    points.append([sh_row['shape_pt_lat'], sh_row['shape_pt_lon']])
                folium.PolyLine(points,
                                color=line_color,
                                tooltip=line_str,
                                weight=3,
                                opacity=1).add_to(feature_groups[line_str])

                df_S = df_stops.loc[(df_stops['trip_id']==trip['trip_id'])]
                for s_index, s_row in df_S.iterrows():
                    stop_info  = df_snames.loc[(df_snames['stop_id'] == s_row['stop_id'])].iloc[0]
                    popup_text = stop_info['stop_name'] + '<br/>(id: ' + stop_info['stop_id'] + ')'
                    folium.CircleMarker(location=[stop_info['stop_lat'],stop_info['stop_lon']],
                                        color=line_color,
                                        radius=3,
                                        fill=True,
                                        fill_color=line_color,
                                        fill_opacity=1,
                                        weight=2,
                                        popup=popup_text,
                                        ).add_to(feature_groups[line_str])

        with open(os.path.join(os.getcwd(), 'network.pkl'), 'wb') as outfile:
            pickle.dump(feature_groups, outfile)
    return feature_groups


def _get_shape_points_between(stop_a: str, stop_b: str, gtfs_dir: str,
                               df_stop_times: pd.DataFrame,
                               df_trips: pd.DataFrame,
                               df_shapes: pd.DataFrame) -> List[Tuple[float,float]]:
    """Retourne les points GPS du tracé GTFS entre deux arrêts consécutifs."""
    trips_a = set(df_stop_times[df_stop_times['stop_id'] == stop_a]['trip_id'])
    trips_b = set(df_stop_times[df_stop_times['stop_id'] == stop_b]['trip_id'])
    common = trips_a & trips_b
    if not common:
        return []

    for trip_id in common:
        t_row = df_trips[df_trips['trip_id'] == trip_id]
        if t_row.empty:
            continue
        shape_id = t_row.iloc[0]['shape_id']
        if pd.isna(shape_id):
            continue

        st = df_stop_times[df_stop_times['trip_id'] == trip_id].sort_values('stop_sequence')
        rows_a = st[st['stop_id'] == stop_a]
        rows_b = st[st['stop_id'] == stop_b]
        if rows_a.empty or rows_b.empty:
            continue

        row_a = rows_a.iloc[0]
        row_b = rows_b.iloc[0]
        if row_a['stop_sequence'] > row_b['stop_sequence']:
            row_a, row_b = row_b, row_a

        d1 = row_a['shape_dist_traveled'] / 1000.0
        d2 = row_b['shape_dist_traveled'] / 1000.0

        pts = df_shapes[
            (df_shapes['shape_id'] == shape_id) &
            (df_shapes['shape_dist_traveled'] >= d1) &
            (df_shapes['shape_dist_traveled'] <= d2)
        ].sort_values('shape_pt_sequence')

        if not pts.empty:
            return list(zip(pts['shape_pt_lat'], pts['shape_pt_lon']))

    return []


def display_route_on_map(df_route, include_kpis=True) -> folium.Map:
    """Given a dataframe representing a route, creates a folium map 
    and adds markers for the stops and lines for the route segments, 
    with the option to also add an automatic title and 2 KPIs: 
     - number of stops in the route
     - total distance of route
    
    Parameters
    ----------
    df_route : pd.DataFrame
      A dataframe representing a route, whereby each row contains
      information on a different stop of the route, and rows are sorted 
      by stop visiting order.
    include_kpis : bool (default=True)
      Whether to include the title and the 2 KPIs in the map

    Returns
    -------
    A folium map that can be displayed or re-used"""
    avg_location = df_route[['latitude', 'longitude']].mean()
    map_route = folium.Map(location=[avg_location['latitude'], avg_location['longitude']], zoom_start=13, tiles="cartodb positron")

    gtfs_dir = os.path.join(os.getcwd(), 'STAN.GTFS')

    for x in _get_lines(gtfs_dir).values():
        x.add_to(map_route)

    # Charger les données GTFS pour le tracé réel
    df_stop_times = pd.read_csv(os.path.join(gtfs_dir, 'stop_times.txt'))
    df_trips      = pd.read_csv(os.path.join(gtfs_dir, 'trips.txt'))
    df_shapes     = pd.read_csv(os.path.join(gtfs_dir, 'shapes.txt'))
    df_sinfo      = pd.read_csv(os.path.join(gtfs_dir, 'stops.txt'))

    # Reconstruire la liste des stop_id depuis df_route (via stop_name + coords)
    stop_ids = []
    for _, row in df_route.iterrows():
        match = df_sinfo[
            (abs(df_sinfo['stop_lat'] - row['latitude']) < 0.0001) &
            (abs(df_sinfo['stop_lon'] - row['longitude']) < 0.0001)
        ]
        if not match.empty:
            stop_ids.append(match.iloc[0]['stop_id'])
        else:
            stop_ids.append(None)

    df_route_segments = _make_route_segments_df(df_route)

    if include_kpis:
        html_title = _get_text_for_title(df_route_segments)
        html_summary = _get_kpis_to_display_on_map(df_route_segments)
        root_map = map_route.get_root()
        root_map.html.add_child(folium.Element(html_title))
        root_map.html.add_child(folium.Element(html_summary))

    for stop in df_route_segments.itertuples():
        initial_stop = stop.Index == 0
        icon = folium.Icon(icon='home' if initial_stop else 'info-sign',
                           color='cadetblue' if initial_stop else 'red')
        marker = folium.Marker(
            location=(stop.latitude, stop.longitude),
            icon=icon,
            tooltip=f"<b>Arrêt</b>: {stop.site} <br>"
                  + f"<b>Position</b>: {stop.Index} <br>"
        )
        marker.add_to(map_route)

        # Tracé réel via shapes GTFS
        sid_a = stop_ids[stop.Index] if stop.Index < len(stop_ids) else None
        sid_b = stop_ids[stop.Index + 1] if stop.Index + 1 < len(stop_ids) else None

        if sid_a and sid_b:
            shape_pts = _get_shape_points_between(sid_a, sid_b, gtfs_dir,
                                                   df_stop_times, df_trips, df_shapes)
        else:
            shape_pts = []

        if shape_pts:
            folium.PolyLine(
                locations=shape_pts,
                tooltip=f"<b>De</b>: {stop.site} <br>"
                      + f"<b>à</b>: {stop.site_next} <br>"
                      + f"<b>Distance</b>: {stop.distance_seg:.0f} m",
                color='grey', weight=8, opacity=0.8
            ).add_to(map_route)
        else:
            # Fallback ligne droite si pas de shape trouvé
            folium.PolyLine(
                locations=[(stop.latitude, stop.longitude),
                           (stop.latitude_next, stop.longitude_next)],
                tooltip=f"<b>De</b>: {stop.site} <br>"
                      + f"<b>à</b>: {stop.site_next} <br>"
                      + f"<b>Distance</b>: {stop.distance_seg:.0f} m",
                color='grey', weight=8, opacity=0.8
            ).add_to(map_route)

    first_stop = df_route.iloc[0][['site', 'latitude', 'longitude']]
    last_stop  = df_route.iloc[-1][['site', 'latitude', 'longitude']]
    is_closed_tour = (first_stop == last_stop).all()

    if not is_closed_tour:
        stop = list(df_route_segments.itertuples())[-1]
        folium.Marker(
            location=(stop.latitude_next, stop.longitude_next),
            tooltip=f"<b>Arrêt</b>: {stop.site_next} <br>"
                  + f"<b>Position</b>: {stop.Index + 1} <br>",
            icon=folium.Icon(prefix='fa', icon='flag-checkered', color='cadetblue')
        ).add_to(map_route)

    return map_route

if __name__ == '__main__':
    df_route = pd.DataFrame(
        [["Jean Jaures", 48.6794599,6.1796641],
         ["Mon Désert - Thermal", 48.6843849,6.1764309],
         ["Gare - Saint-Léon", 48.6875489,6.1740111],
         ["Gare - Saint-Léon", 48.6883529,6.1731961],
         ["Commanderie", 48.6867899,6.1680161]],
        columns=pd.Index(['site', 'latitude', 'longitude'], name='nancy')
    )
    map_html = display_route_on_map(df_route)
    map_html.save("map.html")
    webbrowser.open("map.html")

########################################
# Visualiseur de graphe géographique   #
# utilisé dans le cadre de la SAE 2.02 #
# IUT Nancy Charlemagne                #
# Auteur : Y. Parmentier / IUTNC       #
# Date   : 2026-05-12                  #
# Licence: MIT                         #
########################################

## Bibliothèques utilisées
import multiprocessing ## Multiprocessing requis pour utiliser nicegui en natif
multiprocessing.set_start_method("spawn", force=True)
import sys, asyncio, os.path, platform, shlex, webbrowser, contextlib, argparse
import pandas as pd
f = open(os.devnull, 'w') #Pour ignorer les imports optionnels manquants
sys.stderr = f
from nicegui import ui, native
from visualiser import display_route_on_map

#########################
## CONFIG               #
#########################
gtfs_path="" #réseau
jar_file=""  #projet

#########################
# Fonctions auxiliaires #
#########################
## Exécution d'une commande en arrière plan
## (utilisée pour appeler le programme Java de calcul de
## plus courts chemins)
async def run_command(command: str) -> None:
    """Exécute une commande en arrière plan"""
    print(command)
    process = await asyncio.create_subprocess_exec(
        *shlex.split(command, posix='win' not in sys.platform.lower()),
        stdout=asyncio.subprocess.PIPE, stderr=asyncio.subprocess.STDOUT,
        cwd=os.path.dirname(os.path.abspath(__file__))
    )
    # NOTE il faut lire l'entrée standard par bloc, sinon erreur
    output = ''
    while True:
        new = await process.stdout.read(4096)
        if not new:
            break
        output += new.decode()
        # NOTE le contenu est remplacé à chaque nouvelle chaîne reçue
        content = f'{output}'
        print(content)
        ## On décode le contenu (identifiants d'arrêts séparés par un point-virgule)
        arrets   = content.split(";")
        trajet   = []
        df_sinfo = pd.read_csv(os.path.join(gtfs_path, 'stops.txt'))
        for arret in arrets:
            s_row = df_sinfo.loc[(df_sinfo['stop_id'] == arret.strip())].iloc[0]
            trajet.append([s_row['stop_name'], s_row['stop_lat'], s_row['stop_lon']])
        ## On crée le chemin à afficher
        df_route = pd.DataFrame(
            trajet,
            columns=pd.Index(['site', 'latitude', 'longitude'], name='nancy')
        )
        map_nancy = display_route_on_map(df_route)
        map_nancy.save("map_nancy.html")
        webbrowser.open("map_nancy.html")

### Analyse des données du réseau
def analyse_reseau():
    df_lines  = pd.read_csv(os.path.join(gtfs_path, 'routes.txt'))
    df_trips  = pd.read_csv(os.path.join(gtfs_path, 'trips.txt'))
    df_stops  = pd.read_csv(os.path.join(gtfs_path, 'stop_times.txt'))
    df_sinfo  = pd.read_csv(os.path.join(gtfs_path, 'stops.txt'))
    arrets = []
    # On extrait les arrêts disposés le long des lignes
    for l_index,l_row in df_lines.iterrows():
        # On vérifie que ces arrêts sont associés à au moins un trajet (cf planning horaire)
        t_row = df_trips.loc[(df_trips['route_id']==l_row['route_id']) & df_trips['shape_id'].notnull()]
        if len(t_row) > 0: 
            trip = t_row.iloc[0] #On ne garde que la première occurence (i.e., on ne tient pas compte du planning)
            df_S = df_stops.loc[(df_stops['trip_id']==trip['trip_id'])]
            prev = None
            for s_index, s_row in df_S.iterrows():
                stop_info = df_sinfo.loc[(df_sinfo['stop_id'] == s_row['stop_id'])].iloc[0]
                node_str  = stop_info['stop_name'] + ' [' + stop_info['stop_id'] + ']'
                arrets.append(node_str)
    return sorted(list(set(arrets)))

###########################
## Fonction lancement GUI #
###########################
## Fonction principale qui permet de charger les infos du réseaux et
## de démarrer l'interface graphique
@ui.page('/', title="SAÉ-2.02 Graphes")
def gui():
    arrets = analyse_reseau()

    ui.page_title("SAÉ-2.02 Graphes")
    ui.markdown('''
    ## SAÉ 2.02 - Exploration algorithmique d'un problème (manipulation de graphes)
    
    ### Veuillez choisir votre trajet :
    ''')
    
    with ui.row():
        ui.label('Départ')
        depart = ui.select(options=arrets, with_input=True,
                           on_change=lambda e: print('Sélection du départ : ' + e.value)).classes('w-50')
        ui.label('Arrivée')
        arrivee = ui.select(options=arrets, with_input=True,
                            on_change=lambda e: print('Sélection de l\'arrivée : ' + e.value)).classes('w-50')
        ui.button('Afficher trajet', on_click=lambda: run_command(f'java -jar {jar_file} "{depart.value}" "{arrivee.value}"'))

    ui.label("(liste déroulante avec complétion automatique)")

            
#########################
## Fonction principale  #
#########################

def main(gtfs, jar):
    global gtfs_path
    global jar_file
    if (not os.path.exists(gtfs)):
        print('''
        [ERREUR] Fichiers décrivant le réseau introuvables
                 (chemin du dossier GTFS erroné)
        ''', file=sys.stdout)
        sys.exit(1)
    else:
        gtfs_path=gtfs
    if (not os.path.exists(jar)):
        print('''
        [ERREUR] Fichier jar introuvable
                 (programme de recherche du plus court chemin manquant)
        ''', file=sys.stdout)
        sys.exit(1)
    else:
        jar_file=jar
        with contextlib.suppress(ModuleNotFoundError):
            ui.run(gui, window_size=(900, 300), native=False, reload=platform.system() != 'Windows') ## Version desktop app (a priori pas cross-platform, non testé sur windows)
            #ui.run(gui, native=False, reload=platform.system() != 'Windows') ## version web

        
#########################
## Début script python  #
#########################
if __name__ in {'__main__', "__mp_main__"}:
    parser = argparse.ArgumentParser()
    parser.add_argument('--gtfs', default="STAN.GTFS", help='Chemin du dossier GTFS')
    parser.add_argument('--jar', default="Projet.jar", help='Fichier jar du projet')
    args = parser.parse_args()
    main(args.gtfs, args.jar)

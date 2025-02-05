<?php
date_default_timezone_set("America/Bogota");
require("controlador/conexion.php");
session_start();

if (@$_SESSION['logged'] == 'yes') {
  $acc = $_SESSION['acc'];
  require_once("inc/init.php");
  require_once("inc/config.ui.php");
  $page_title = "NOMBRE PAGINA";
  $page_css[] = "your_style.css";
  include("inc/header.php");
  $page_nav["menu_ppal"]["active"] = true;
  include("inc/nav.php");
?>

  <!-- ==========================CONTENT STARTS HERE ========================== -->
  <!-- MAIN PANEL -->
  <div class="container">
  <style>
    /* Contenedor del mapa con diseño responsivo */
    #map {
      height: 50vh;
      /* Altura: 50% de la ventana */
      width: 90%;
      /* Ancho: 90% del ancho del dispositivo */
      max-width: 1200px;
      /* Ancho máximo en dispositivos grandes */
      margin: 0 auto;
      margin-top: 13px;
      /* Centrar el mapa */
      border: 1px solid #ccc;
      /* Opcional: Borde alrededor del mapa */
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
      /* Sombra para diseño limpio */
    }

    /* Ajustes para dispositivos muy pequeños */
    @media (max-width: 600px) {
      #map {
        height: 60vh;
        /* Mayor altura en dispositivos pequeños */
      }
    }
  </style>
    <div id="map" align="center"></div>
    <br>
    <div class="tecnicos">
      <div class="row">
        <?php
        $consulta2 = "SELECT u.nombre_1, u.apellido_2, color 
                        FROM usuario as u JOIN usuario_has_profile as up ON (u.id_usuario = up.id_usuario) 
                        WHERE up.id_perfil = 3 OR up.id_perfil = 6 AND up.id_estado_usuario = 1";
        $resultado = mysqli_query($con, $consulta2);

        $datos = [];
        while ($fila = mysqli_fetch_assoc($resultado)) {
          $datos[] = $fila;
        }

        // Número total de filas
        $total_filas = count($datos);

        // Dividir los datos en módulos
        $modulo = ceil($total_filas / 4);
        ?>
        <div align="center">
          <?php
          // Recorrer los datos con un for
          for ($i = 0; $i < $total_filas; $i++) {
            // Abrir un nuevo div de columna y tabla al inicio de cada grupo
            if ($i % $modulo == 0) {
              if ($i > 0) { // Cerrar la tabla anterior si no es el primer grupo
                echo "</tbody></table></div>";
              }
              echo "<div class='col-md-3'>"; // Div para la columna
              echo "<table class='table table-striped'>";
              echo "<thead>";
              echo "<tr>";
              echo "<th scope='col'>Nombre</th>";
              echo "<th scope='col'>Color</th>";
              echo "</tr>";
              echo "</thead>";
              echo "<tbody>";
            }

            // Mostrar los datos en una fila
            echo "<tr>";
            echo "<td>" . $datos[$i]["nombre_1"] . " " . $datos[$i]["apellido_2"] . "</td>";
            echo "<td style='background-color: " . $datos[$i]["color"] . "; color: white;'>" . $datos[$i]["color"] . "</td>";
            echo "</tr>";
          }

          // Cerrar la última tabla y div de columna
          echo "</tbody></table></div>";
          ?>
        </div>
      </div>
    </div>
  </div>


  
  <script>
    let map; // Referencia al mapa
    let markers = []; // Array para guardar los marcadores actuales

    async function fetchMarkers() {
      try {
        // Obtener los datos del archivo JSON
        const response = await fetch("./Tecnicos/users/usuarios.json");
        const data = await response.json();
        return data; // JSON en formato de array
      } catch (error) {
        console.error("Error al obtener los marcadores:", error);
        return [];
      }
    }

    function clearMarkers() {
      // Eliminar todos los marcadores del mapa
      markers.forEach((marker) => marker.setMap(null));
      markers = [];
    }

    function updateMarkers(newMarkers) {
      clearMarkers(); // Limpiar marcadores actuales

      // Crear nuevos marcadores
      newMarkers.forEach((markerData) => {
        const MAP_MARKER = 'M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z';
        // Verificar que las coordenadas sean válidas
        const lat = parseFloat(markerData.latitud);
        const lng = parseFloat(markerData.altitud);

        if (!isNaN(lat) && !isNaN(lng)) {
          const marker = new google.maps.Marker({
            position: {
              lat: lat,
              lng: lng
            },
            map: map,
            label: {
              color: "black", // Color del texto
              fontSize: "20px", // Tamaño de fuente
              fontWeight: "bold" // Peso de la fuente
            },
            icon: {
              path: MAP_MARKER,
              fillColor: markerData.color,
              fillOpacity: 1,
              anchor: {
                x: 12,
                y: 24
              },
            },
            title: `ID Usuario: ${markerData.id_usuario || "Sin ID"}`, // Tooltip al pasar el mouse
          });
          markers.push(marker); // Guardar el marcador en el array
        }
      });
    }


    async function initMap() {
      // Inicializar el mapa
      map = new google.maps.Map(document.getElementById("map"), {
        center: {
          lat: 4.5837978,
          lng: -74.1900547
        },
        zoom: 14,
      });

      // Cargar y actualizar marcadores cada 5 segundos
      setInterval(async () => {
        const newMarkers = await fetchMarkers();
        updateMarkers(newMarkers);
      }, 5000);
    }
  </script>
  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDc4Np5rNKB-1gZ7fopMp8bZU54F40-1VM&callback=initMap" defer></script>


  <!-- END MAIN PANEL -->
  <!-- ==========================CONTENT ENDS HERE ========================== -->

  <?php
  include("inc/footer.php");
  include("inc/scripts.php");
  ?>
  <script src="/js/plugin/flot/jquery.flot.cust.min.js"></script>
  <script src="/js/plugin/flot/jquery.flot.resize.min.js"></script>
  <script src="/js/plugin/flot/jquery.flot.time.min.js"></script>
  <script src="/js/plugin/flot/jquery.flot.tooltip.min.js"></script>
  <script src="/js/plugin/vectormap/jquery-jvectormap-1.2.2.min.js"></script>
  <script src="/js/plugin/vectormap/jquery-jvectormap-world-mill-en.js"></script>
  <script src="/js/plugin/moment/moment.min.js"></script>
  <script src="/js/plugin/fullcalendar/jquery.fullcalendar.min.js"></script>
<?php

} else {
  header("Location:index.php");
}
?>
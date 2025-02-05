<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE");
header("Allow: GET, POST, OPTIONS, PUT, DELETE");


class Conexion {
    private $hostname = "localhost";
    private $username = "johan";
    private $password = "Johan1994";
    private $dbname = "inttelgo";

    private $mysqlConexion;
    private $resultado;

    // Abre la conexión a la base de datos
    public function abrirConexion() {
        $this->mysqlConexion = new mysqli($this->hostname, $this->username, $this->password, $this->dbname);
        $this->mysqlConexion->set_charset("utf8");  // Para evitar problemas con acentos y caracteres especiales

        // Manejo de errores de conexión
        if ($this->mysqlConexion->connect_error) {
            die("Error de conexión: " . $this->mysqlConexion->connect_error);
        }
    }
    // Ejecuta una consulta SQL
    public function ejecutarConsulta($sentenciaSQL) {
        $this->resultado = $this->mysqlConexion->query($sentenciaSQL);

        if (!$this->resultado) {
            die("Error en la consulta: " . $this->mysqlConexion->error);
        }
    }

    // Obtiene el siguiente registro de un resultado
    public function siguienteRegistro() {
        return $this->resultado->fetch_row();
    }

    // Obtiene el ID autonumérico generado
    public function obtenerLlaveAutonumerica() {
        return $this->mysqlConexion->insert_id;
    }

    // Cierra la conexión
    public function cerrarConexion() {
        $this->mysqlConexion->close();
    }

    // Obtiene el número de filas de un resultado
    public function numeroFilas() {
        return $this->resultado->num_rows;
    }
}
?>
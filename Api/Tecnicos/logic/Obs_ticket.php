<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/Obs_ticketDAO.php"); 
class Obs_ticket{

    private $id_obs_ticket;
    private $obs;
    private $id_ticket;
    private $fecha;
    private $id_user;

    public function setId_obs_ticket($id_obs_ticket){
        $this->id_obs_ticket = $id_obs_ticket;
    }
    
    public function getId_obs_ticket(){
        return $this->id_obs_ticket;
    }
    
    public function setObs($obs){
        $this->obs = $obs;
    }
    
    public function getObs(){
        return $this->obs;
    }
    
    public function setId_ticket($id_ticket){
        $this->id_ticket = $id_ticket;
    }
    
    public function getId_ticket(){
        return $this->id_ticket;
    }
    
    public function setFecha($fecha){
        $this->fecha = $fecha;
    }
    
    public function getFecha(){
        return $this->fecha;
    }

    public function setId_user($id_user){
        $this->id_user = $id_user;
    }

    public function getId_user(){
        return $this->id_user;
    }

    public function __construct($id_obs_ticket=0, $obs="", $id_ticket=0, $fecha=null, $id_user=null){
        $this->id_obs_ticket = $id_obs_ticket;
        $this->obs = $obs;
        $this->id_ticket = $id_ticket;
        $this->fecha = $fecha;
        $this->id_user = $id_user;
    }

    public function consultarHistorial(){
        $obs_tickets = array();
        $tenicos = array();
        $conexion = new Conexion(); 
        $conexion->abrirConexion();
        $obs_ticketDAO = new Obs_ticketDAO(null, null,$this->id_ticket);
        $conexion->ejecutarConsulta($obs_ticketDAO->consultarHistorial());
        while($resultado = $conexion->siguienteRegistro()){
            $tecnico = null;
            if(array_key_exists($resultado[3], $tenicos)){
                $tecnico = $tenicos[$resultado[3]]; 
            }else{
                $tecnico = new Tecnico(null, null,$resultado[3]);
                $tecnico->consultarPorId();
                $tenicos[$resultado[3]] = $tecnico;
            }
            $obs_ticket = new Obs_ticket($resultado[0], $resultado[1], $this->id_obs_ticket, $resultado[2], $tecnico);
            array_push($obs_tickets, $obs_ticket);
        }
        $conexion->cerrarConexion();
        return $obs_tickets;
    }

    public function guardar(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $obs_ticketDAO = new Obs_ticketDAO(null, $this->obs,$this->id_ticket, $this->fecha, $this->id_user);
        $conexion->ejecutarConsulta($obs_ticketDAO->guardar());
        $num = $conexion->obtenerLlaveAutonumerica();
        $conexion->cerrarConexion();
        return $num;
    }

    public function guardarFinalizar($tecnico){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $obs_ticketDAO = new Obs_ticketDAO(null, $this->obs,$this->id_ticket, $this->fecha, $this->id_user);
        $conexion->ejecutarConsulta($obs_ticketDAO->guardar());
        $num = $conexion->obtenerLlaveAutonumerica();
        $conexion->ejecutarConsulta($obs_ticketDAO->finalizarTicket($tecnico));
        $conexion->cerrarConexion();
        return $num;
    }

    public function guardarI($tecnico){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $obs_ticketDAO = new Obs_ticketDAO(null, $this->obs,$this->id_ticket, $this->fecha);
        $conexion->ejecutarConsulta($obs_ticketDAO->guardarI($tecnico));
        $num = $conexion->obtenerLlaveAutonumerica();
        $conexion->cerrarConexion();
    }
}
?>
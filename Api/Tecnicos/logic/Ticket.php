<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/TicketDAO.php"); 
class Ticket {
    private $id_ticket;
    private $estado;//estado: Activo = 1 inactivo = 2
    private $tipo;
    private $cliente;
    private $observacion_u; // observacion que realizo el de administracion
    private $observacion_final; //ultima observacion realizada por el tecnico

    private $fecha_hora; //hora que se creo el ticket
    private $fecha_hora_final; //hora cuando termino el soporte

    private $tecnico;

    public function setId_ticket($id_ticket){
        $this->id_ticket = $id_ticket;
    }
    
    public function getId_ticket(){
        return $this->id_ticket;
    }
    
    public function setEstado($estado){
        $this->estado = $estado;
    }
    
    public function getEstado(){
        return $this->estado;
    }
    
    public function setTipo($tipo){
        $this->tipo = $tipo;
    }
    
    public function getTipo(){
        return $this->tipo;
    }
    
    public function setCliente($cliente){
        $this->cliente = $cliente;
    }
    
    public function getCliente(){
        return $this->cliente;
    }
    
    public function setObservacion_u($observacion_u){
        $this->observacion_u = $observacion_u;
    }
    
    public function getObservacion_u(){
        return $this->observacion_u;
    }
    
    public function setObservacion_final($observacion_final){
        $this->observacion_final = $observacion_final;
    }
    
    public function getObservacion_final(){
        return $this->observacion_final;
    }
    
    public function setFecha_hora($fecha_hora){
        $this->fecha_hora = $fecha_hora;
    }
    
    public function getFecha_hora(){
        return $this->fecha_hora;
    }
    
    public function setFecha_hora_final($fecha_hora_final){
        $this->fecha_hora_final = $fecha_hora_final;
    }
    
    public function getFecha_hora_final(){
        return $this->fecha_hora_final;
    }
    
    public function setTecnico($tecnico){
        $this->tecnico = $tecnico;
    }
    
    public function getTecnico(){
        return $this->tecnico;
    }
    


    public function __construct($id_ticket=0, $estado=0, $tipo="", $cliente=null, $observacion_u="", $observacion_final="", $fecha_hora=null, $fecha_hora_final=null, $tecnico=null){
        $this->id_ticket = $id_ticket;
        $this->estado = $estado;
        $this->tipo = $tipo;
        $this->cliente = $cliente;
        $this->observacion_u = $observacion_u;
        $this->observacion_final = $observacion_final;
        $this->fecha_hora = $fecha_hora;
        $this->fecha_hora_final = $fecha_hora_final;
        $this->tecnico = $tecnico;
    }

    public function consultarTodos(){//se consulta por estado: Activo = 1 inactivo = 2
        $tickets = array();
        $tipos = array();
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $ticketDAO = new TicketDAO();
        $conexion->ejecutarConsulta($ticketDAO->consultarTodos());
        while($resultado = $conexion->siguienteRegistro()){
            $tipo = null;
            if(array_key_exists($resultado[1], $tipos)){
                $tipo = $tipos[$resultado[1]];
            }else{
                $tipo = new Tipo($resultado[1]);
                $tipo -> consultar();
                $tipos[$resultado[1]] = $tipo;
            }
            $cliente = new Cliente($resultado[2]);
            $cliente->consultarPorIdConBarrio();
            $ticket = new Ticket($resultado[0],1, $tipo, $cliente, $resultado[3], null, $resultado[4]);

            array_push($tickets, $ticket);
        }
        $conexion->cerrarConexion();
        return $tickets;
    }

    public function consultarPorId(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $ticketDAO = new TicketDAO($this->id_ticket);
        $conexion->ejecutarConsulta($ticketDAO->consultarPorId());
        if($conexion->numeroFilas() > 0){
            $resultado = $conexion->siguienteRegistro();
            $tipo = new Tipo($resultado[0]);
            $tipo->consultar();
            $this->tipo = $tipo;
            $cliente = new Cliente($resultado[1]);
            $cliente->consultarPorId();
            $this->cliente = $cliente;
            $this->fecha_hora = $resultado[2];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }

    public function consultarPorPrioridad($prioridad){
        $tickets = array();
        $tipos = array();
        $barrios = array();
        $conexion = new Conexion();
        $conexion->abrirConexion();
        if($prioridad == 1){
            $fecha_actual = new DateTime();
            // Restar 8 horas
            $fecha_actual->modify('-8 hours');
            $this->fecha_hora = $fecha_actual->format('Y-m-d H:i:s');
            $ticketDAO = new TicketDAO(null,1,null,null,null,null,$this->fecha_hora);
            $consulta = $ticketDAO->consultarMayorQue();
        }else if($prioridad == 2){
            $fecha_actual = new DateTime();
            // Restar 8 horas
            $fecha_actual->modify('-8 hours');
            $this->fecha_hora = $fecha_actual->format('Y-m-d H:i:s');
            $ticketDAO = new TicketDAO(null,1,null,null,null,null,$this->fecha_hora);
            // Restar 16 horas
            $fecha_actual->modify('-8 hours');
            // Consultar tickets entre la fecha_actual y 8 horas antes
            $consulta = $ticketDAO->consultarEntre($fecha_actual->format('Y-m-d H:i:s'));
        }else{
            $fecha_actual = new DateTime();
            // Restar 16 horas
            $fecha_actual->modify('-16 hours');
            $this->fecha_hora = $fecha_actual->format('Y-m-d H:i:s');
            $ticketDAO = new TicketDAO(null,1,null,null,null,null,$this->fecha_hora);
            $consulta = $ticketDAO->consultarMenorQue();
        }
        //echo $consulta;
        $conexion->ejecutarConsulta($consulta);
        while($resultado = $conexion->siguienteRegistro()){
            $tipo = null;
            if(array_key_exists($resultado[1], $tipos)){
                $tipo = $tipos[$resultado[1]];
            }else{
                $tipo = new Tipo($resultado[1]);
                $tipo -> consultar();
                $tipos[$resultado[1]] = $tipo;
            }
            $cliente = new Cliente($resultado[2]);
            $cliente->consultarPorIdConBarrio();
            $ticket = new Ticket($resultado[0],1, $tipo, $cliente, $resultado[3], null, $resultado[4]);

            array_push($tickets, $ticket);
        }
        $conexion->cerrarConexion();
        return $tickets;
    }
}
?>
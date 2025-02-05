<?php 
class Obs_ticketDAO{

    private $id_obs_ticket;
    private $obs;
    private $id_ticket;
    private $fecha;

    private $id_user;

    public function __construct($id_obs_ticket=0, $obs="", $id_ticket=0, $fecha=null, $id_user=0){
        $this->id_obs_ticket = $id_obs_ticket;
        $this->obs = $obs;
        $this->id_ticket = $id_ticket;
        $this->fecha = $fecha;
        $this->id_user = $id_user;
    }

    public function consultarHistorial(){
        return "SELECT id_obs_ticket, obs, fecha, id_usuario
                FROM obs_ticket
                WHERE id_ticket = ".$this->id_ticket."
        ";
    }

    public function guardar(){
        return "INSERT INTO obs_ticket (obs, id_ticket, fecha, id_usuario)
                VALUES (".$this->obs.", ".$this->id_ticket.", ".$this->fecha.", ".$this->id_user.")
        ";
    }

    public function finalizarTicket($tecnico){
        return "UPDATE ticket
                SET id_estado_ticket = 2, observacion_ticket_tec = $this->obs, fecha_hora_tec =$this->fecha, id_usuario_fin = $tecnico
                WHERE id_ticket = ".$this->id_ticket."
        ";
    }

    public function guardarI($tecnico){
        return "INSERT INTO obs_instalacion (obs_instalacion, id_instalacion, fecha, id_usuario)
                VALUES (".$this->obs.", ".$this->id_ticket.", ".$this->fecha.", ".$tecnico.")
        ";
    }
}
?>
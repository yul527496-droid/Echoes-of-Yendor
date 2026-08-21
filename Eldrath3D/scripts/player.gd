extends CharacterBody3D

const WALK_SPEED := 4.8
const RUN_SPEED := 8.0
const ACCELERATION := 24.0
const MOUSE_SENSITIVITY := 0.0022
const JUMP_VELOCITY := 6.5

var gravity: float = 18.0
var yaw := 0.0
var pitch := -0.12
var camera_pivot: Node3D
var camera: Camera3D
var was_jump_down := false

func _ready() -> void:
	name = "Sinder_Prototype"
	gravity = float(ProjectSettings.get_setting("physics/3d/default_gravity", 18.0))
	_build_body()
	_build_camera()
	Input.mouse_mode = Input.MOUSE_MODE_CAPTURED

func _build_body() -> void:
	var collision := CollisionShape3D.new()
	var capsule_shape := CapsuleShape3D.new()
	capsule_shape.radius = 0.42
	capsule_shape.height = 1.78
	collision.shape = capsule_shape
	collision.position.y = 0.89
	add_child(collision)

	var body_mesh := MeshInstance3D.new()
	var capsule_mesh := CapsuleMesh.new()
	capsule_mesh.radius = 0.42
	capsule_mesh.height = 1.78
	body_mesh.mesh = capsule_mesh
	body_mesh.position.y = 0.89
	var mat := StandardMaterial3D.new()
	mat.albedo_color = Color(0.13, 0.135, 0.15)
	mat.roughness = 0.92
	body_mesh.material_override = mat
	add_child(body_mesh)

	var mantle := MeshInstance3D.new()
	var mantle_mesh := BoxMesh.new()
	mantle_mesh.size = Vector3(0.95, 0.65, 0.18)
	mantle.mesh = mantle_mesh
	mantle.position = Vector3(0.0, 1.28, 0.12)
	var mantle_mat := StandardMaterial3D.new()
	mantle_mat.albedo_color = Color(0.07, 0.075, 0.085)
	mantle_mat.roughness = 1.0
	mantle.material_override = mantle_mat
	add_child(mantle)

func _build_camera() -> void:
	camera_pivot = Node3D.new()
	camera_pivot.name = "CameraPivot"
	camera_pivot.position = Vector3(0.0, 1.45, 0.0)
	add_child(camera_pivot)

	camera = Camera3D.new()
	camera.name = "ThirdPersonCamera"
	camera.position = Vector3(0.0, 0.55, 4.9)
	camera.fov = 72.0
	camera.current = true
	camera_pivot.add_child(camera)
	_update_camera_rotation()

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventMouseMotion and Input.mouse_mode == Input.MOUSE_MODE_CAPTURED:
		yaw -= event.relative.x * MOUSE_SENSITIVITY
		pitch = clamp(pitch - event.relative.y * MOUSE_SENSITIVITY, -0.75, 0.5)
		_update_camera_rotation()
	elif event is InputEventKey and event.pressed and event.keycode == KEY_ESCAPE:
		Input.mouse_mode = Input.MOUSE_MODE_VISIBLE if Input.mouse_mode == Input.MOUSE_MODE_CAPTURED else Input.MOUSE_MODE_CAPTURED
	elif event is InputEventMouseButton and event.pressed and Input.mouse_mode != Input.MOUSE_MODE_CAPTURED:
		Input.mouse_mode = Input.MOUSE_MODE_CAPTURED

func _update_camera_rotation() -> void:
	if camera_pivot:
		camera_pivot.rotation = Vector3(pitch, yaw - rotation.y, 0.0)

func _physics_process(delta: float) -> void:
	var x := float(Input.is_physical_key_pressed(KEY_D)) - float(Input.is_physical_key_pressed(KEY_A))
	var z := float(Input.is_physical_key_pressed(KEY_S)) - float(Input.is_physical_key_pressed(KEY_W))
	var input_vec := Vector2(x, z)
	if input_vec.length() > 1.0:
		input_vec = input_vec.normalized()

	var forward := Vector3(-sin(yaw), 0.0, -cos(yaw))
	var right := Vector3(cos(yaw), 0.0, -sin(yaw))
	var direction := (right * input_vec.x + forward * -input_vec.y).normalized()
	var running := Input.is_physical_key_pressed(KEY_SHIFT)
	var target_speed := RUN_SPEED if running else WALK_SPEED
	var target_velocity := direction * target_speed

	velocity.x = move_toward(velocity.x, target_velocity.x, ACCELERATION * delta)
	velocity.z = move_toward(velocity.z, target_velocity.z, ACCELERATION * delta)

	if direction.length_squared() > 0.001:
		var target_yaw := atan2(-direction.x, -direction.z)
		rotation.y = lerp_angle(rotation.y, target_yaw, min(1.0, 12.0 * delta))
		_update_camera_rotation()

	if not is_on_floor():
		velocity.y -= gravity * delta
	else:
		velocity.y = -0.2

	var jump_down := Input.is_physical_key_pressed(KEY_SPACE)
	if jump_down and not was_jump_down and is_on_floor():
		velocity.y = JUMP_VELOCITY
	was_jump_down = jump_down

	move_and_slide()

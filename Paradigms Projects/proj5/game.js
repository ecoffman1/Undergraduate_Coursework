let game = null;
const id = Math.floor(Math.random() * 10000000);
let last_update_time = new Date();

// Represents a moving image
class Sprite {
	constructor(x, y, image_url, id) {
		this.x = x;
		this.y = y;
		this.id = id;
        this.speed = 120; // pixels-per-second
		this.image = new Image();
		this.image.src = image_url;

        // Set some default event handlers
		this.update = Sprite.prototype.update_stop;
		this.onleftclick = Sprite.prototype.onclick_ignore;
        this.onrightclick = Sprite.prototype.onclick_ignore;
        this.arrive = Sprite.prototype.update_stop;
	}

    // The default update behavior
	update_stop(elapsed_time) {
        delete this.dist_remaining; // causes the object to stop having the property
	}

    // Move forward
	update_travel(elapsed_time) {
		if(this.dist_remaining === undefined)
			return; // No destination to travel toward
        let dist_step = Math.min(this.dist_remaining, elapsed_time * this.speed);
        this.x += dist_step * this.component_x;
        this.y += dist_step * this.component_y;
        this.dist_remaining = this.dist_remaining - dist_step;
        if (this.dist_remaining === 0)
           this.arrive();
	}

    // Remove "this" from the list of sprites
    update_disappear(elapsed_time) {
        for (let i = 0; i < game.model.sprites.length; i++) {
            if (game.model.sprites[i] === this) {
                game.model.sprites.splice(i, 1); // remove this sprite from the list
                return;
            }
        }
        console.log('uh oh, I could not find this sprite in model.sprites!');
    }

    // Do nothing
	onclick_ignore(x, y) {
	}

    // Start travelling to the spot clicked
	onclick_set_destination(x, y) {
        let delta_x = x - this.x;
        let delta_y = y - this.y;
        this.dist_remaining = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
		this.component_x = delta_x / this.dist_remaining;
		this.component_y = delta_y / this.dist_remaining;
	}

    // Throw a fireball toward the spot clicked
    onclick_throw_fireball(x, y) {
		let fireball = new Sprite(this.x, this.y, "fireball.png", id);
        fireball.speed = 300; // pixels-per-second
        fireball.update = Sprite.prototype.update_travel;
        fireball.arrive = Sprite.prototype.update_disappear;
        let delta_x = x - this.x;
        let delta_y = y - this.y;
        fireball.dist_remaining = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        fireball.component_x = delta_x / fireball.dist_remaining;
        fireball.component_y = delta_y / fireball.dist_remaining;
		game.model.sprites.push(fireball);
    }
}




class Model {
	constructor() {
		this.sprites = [];
		this.used = [];

        // Make the avatar
		this.avatar = new Sprite(500, 250, "robot.png",id);
        this.avatar.update = Sprite.prototype.update_travel;
        this.avatar.onleftclick = Sprite.prototype.onclick_set_destination;
        this.avatar.onrightclick = Sprite.prototype.onclick_throw_fireball;
		this.sprites.push(this.avatar);

        this.last_update_time = new Date();

		let payload = {
			id: id,
		};
		fetch('connect', {
			body: JSON.stringify(payload),
			cache: "no-cache",
			headers: {
				'Content-Type': 'application/json',
			},
			method: "POST",
		})
		.then(response => response.text())
		.then(text => {
			console.log(`The server replied ${text}`);
		})
		.catch(ex => {
			console.log(`An error occurred: ${ex}\n${ex.stack}`);
		});
	}

	update() {
        let now = new Date();
        let elapsed_time = (now - this.last_update_time) / 1000; // seconds
        // Update all the sprites
		for (const sprite of this.sprites) {
			sprite.update(elapsed_time);
		}

        this.last_update_time = now;
	}

	add(ob){
		if(ob.id === id){
			return;
		}
		for(const sprite of this.sprites){
			if(sprite.speed === 300){
				continue;
			}
			if(ob.id === sprite.id){
				if(ob.x != sprite.x || ob.y != sprite.y){
					sprite.onleftclick(ob.x, ob.y);
				}
				return;
			}
		}
		let other = new Sprite(ob.x, ob.y, "robot.png",ob.id);
		other.update = Sprite.prototype.update_travel;
        other.onleftclick = Sprite.prototype.onclick_set_destination;
        other.onrightclick = Sprite.prototype.onclick_throw_fireball;
		this.sprites.push(other);
	}

	fire(ob){
		if(ob.id === id){
			return;
		}
		for(const sprite of this.sprites){
			if(sprite.speed === 300){
				continue;
			}
			console.log(this.used.indexOf(ob.id) === -1);
			console.log(ob.id);
			if(ob.id === sprite.id && this.used.indexOf(ob.who) === -1){
				sprite.onrightclick(ob.x, ob.y);
				this.used.push(ob.who);
				return;
			}
		}
	}

	onleftclick(x, y) {
		this.avatar.onleftclick(x, y);
	}

    onrightclick(x, y) {
		this.avatar.onrightclick(x, y);
    }
}




class View
{
	constructor(model) {
		this.model = model;
		this.canvas = document.getElementById("myCanvas");
	}

	update() {
        // Clear the screen
		let ctx = this.canvas.getContext("2d");
		ctx.clearRect(0, 0, 1000, 500);

        // Sort the sprites by their y-value to create a pseudo-3D effect
        this.model.sprites.sort((a,b) => a.y - b.y );

        // Draw all the sprites
		for (const sprite of this.model.sprites) {
			ctx.drawImage(sprite.image, sprite.x - sprite.image.width / 2, sprite.y - sprite.image.height);
		}
	}
}




class Controller
{
	constructor(model, view) {
		this.model = model;
		this.view = view;
		let self = this;

        // Add event listeners
		view.canvas.addEventListener("click", function(event) { self.onLeftClick(event); return false; });
		view.canvas.addEventListener("contextmenu", function(event) { self.onRightClick(event); return false; });
	}

	

	onLeftClick(event) {
        event.preventDefault(); 
		const x = event.pageX - this.view.canvas.offsetLeft;
		const y = event.pageY - this.view.canvas.offsetTop;
		this.model.onleftclick(x, y);

        // todo: tell the server about this click
		let payload = {
			id: id,
			x: x,
			y: y,
		};
		fetch('leftClick', {
			body: JSON.stringify(payload),
			cache: "no-cache",
			headers: {
				'Content-Type': 'application/json',
			},
			method: "POST",
		})
		.then(response => response.text())
		.then(text => {
			console.log(`The server replied ${text}`);
		})
		.catch(ex => {
			console.log(`An error occurred: ${ex}\n${ex.stack}`);
		});
	}

    onRightClick(event) {
        event.preventDefault(); // Suppress the context menu
		const x = event.pageX - this.view.canvas.offsetLeft;
		const y = event.pageY - this.view.canvas.offsetTop;
		this.model.onrightclick(x, y);

        // todo: tell the server about this click
		let payload = {
			id: id,
			x: x,
			y: y,
		};
		fetch('rightClick', {
			body: JSON.stringify(payload),
			cache: "no-cache",
			headers: {
				'Content-Type': 'application/json',
			},
			method: "POST",
		})
		.then(response => response.text())
		.then(text => {
			console.log(`The server replied ${text}`);
		})
		.catch(ex => {
			console.log(`An error occurred: ${ex}\n${ex.stack}`);
		});
		
    }

	update() {
        // Ensure we do not hammer the server with too many update requests
        let now = new Date();
        if (now - last_update_time > 500) { // miliseconds
			last_update_time = now;
			//Collect all players from server
			let payload = {
				id: id,
			};
			fetch('update', {
				body: JSON.stringify(payload),
				cache: "no-cache",
				headers: {
					'Content-Type': 'application/json',
				},
				method: "POST",
			})
			.then(response => response.text())
			.then(text => {
				console.log(`The server replied ${text}`);
				let response_ob = JSON.parse(text);
				let l = Object.keys(response_ob);
				for(let el of l){
					console.log(el);
					console.log(response_ob[el]);
				}
				let fireballs = response_ob.fireballs;
				let players = response_ob.players;

				for (let i = 0; i < players.length; i++){
					this.model.add(players[i]);
				}
				for (let i = 0; i < fireballs.length; i++){
					this.model.fire(fireballs[i]);
				}
			})
			.catch(ex => {
				console.log(`An error occurred: ${ex}\n${ex.stack}`);
			});
        }
	}
}




class Game {
	constructor() {
		this.model = new Model();
		this.view = new View(this.model);
		this.controller = new Controller(this.model, this.view);
	}

	onTimer() {
		this.controller.update();
		this.model.update();
		this.view.update();
	}
}


game = new Game();
let timer = setInterval(() => { game.onTimer(); }, 30);

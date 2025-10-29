const id: number = Math.floor(Math.random() * 10000000);
let last_update_time: Date = new Date();
let person: string | null = sessionStorage.getItem("name");

// Represents a moving image
class Sprite {
    x: number;
    y: number;
    id: number;
    speed: number;
    image: HTMLImageElement;
    dist_remaining?: number;
    component_x?: number;
    component_y?: number;
    update: (elapsed_time: number) => void;
    onleftclick: (x: number, y: number) => void;
    onrightclick: (x: number, y: number) => void;
    arrive: () => void;
    name?: string;

    constructor(x: number, y: number, image_url: string, id: number) {
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

    gethitbox() {
        return {
            l: this.x - this.image.width / 2,
            r: this.x + this.image.width / 2,
            t: this.y - this.image.height,
            b: this.y,
        };
    }

    // The default update behavior
    update_stop(): void {
        delete this.dist_remaining; // causes the object to stop having the property
    }

    // Move forward
    update_travel(elapsed_time: number): void {
        if (this.dist_remaining === undefined) return; // No destination to travel toward
        let dist_step = Math.min(this.dist_remaining, elapsed_time * this.speed);
        this.x += dist_step * (this.component_x ?? 0);
        this.y += dist_step * (this.component_y ?? 0);
        this.dist_remaining = this.dist_remaining - dist_step;
        if (this.dist_remaining === 0) this.arrive();
        if (this.speed === 300 && this.id === id) {
            game?.model.checkcollide(this);
        }
    }

    // Remove "this" from the list of sprites
    update_disappear(): void {
        for (let i = 0; i < game.model.sprites.length; i++) {
            if (game?.model.sprites[i] === this) {
                game?.model.sprites.splice(i, 1); // remove this sprite from the list
                return;
            }
        }
        console.log('uh oh, I could not find this sprite in model.sprites!');
    }

    // Do nothing
    onclick_ignore(x: number, y: number): void {}

    // Start travelling to the spot clicked
    onclick_set_destination(x: number, y: number): void {
        let delta_x = x - this.x;
        let delta_y = y - this.y;
        this.dist_remaining = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        this.component_x = delta_x / this.dist_remaining;
        this.component_y = delta_y / this.dist_remaining;
    }

    // Throw a fireball toward the spot clicked
    onclick_throw_fireball(x: number, y: number): void {
        let fireball = new Sprite(this.x, this.y, "fireball.png", this.id);
        fireball.speed = 300; // pixels-per-second
        fireball.update = Sprite.prototype.update_travel;
        fireball.arrive = Sprite.prototype.update_disappear;
        let delta_x = x - this.x;
        let delta_y = y - this.y;
        fireball.dist_remaining = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        fireball.component_x = delta_x / fireball.dist_remaining;
        fireball.component_y = delta_y / fireball.dist_remaining;
        game?.model.sprites.push(fireball);
    }
}

class Model {
    sprites: Sprite[] = [];
    used: { [key: string]: boolean } = {};
    img_paths: string[] = [
        "chair", "lamp", "mushroom", "outhouse", "pillar", "pond", "rock", "statue", "tree", "turtle",
    ];
    avatar: Sprite;
    last_update_time: Date;

    constructor() {
        this.avatar = new Sprite(500, 250, "robot.png", id);
        this.avatar.update = Sprite.prototype.update_travel;
        this.avatar.onleftclick = Sprite.prototype.onclick_set_destination;
        this.avatar.onrightclick = Sprite.prototype.onclick_throw_fireball;
        this.avatar.name = person ?? "Unknown"; // Ensure name is a valid string
        this.sprites.push(this.avatar);
        this.connect();
        this.last_update_time = new Date();
    }

    async connect() {
        const payload = {
            id: id,
            name: person,
        };
        let response = await fetch('connect', {
            body: JSON.stringify(payload),
            cache: "no-cache",
            headers: { 'Content-Type': 'application/json' },
            method: "POST",
        });
        const text = await response.text();
        console.log(`The server replied ${text}`);
        let response_ob = JSON.parse(text);
        for (let item of response_ob) {
            let mapitem = new Sprite(item.x, item.y, 'images/' + this.img_paths[item.type] + '.png', id);
            this.sprites.push(mapitem);
        }
    }

    update(): void {
        let now = new Date();
        let elapsed_time = (now.getTime() - this.last_update_time.getTime()) / 1000; // seconds
        // Update all the sprites
        for (const sprite of this.sprites) {
            sprite.update(elapsed_time);
        }
        this.last_update_time = now;
    }

    add(ob: any, oid: number): void {
        if (oid === id) {
            return;
        }
        for (const sprite of this.sprites) {
            if (sprite.speed === 300) {
                continue;
            }
            if (oid === sprite.id) {
                if (ob.x !== sprite.x || ob.y !== sprite.y) {
                    sprite.onleftclick(ob.x, ob.y);
                }
                return;
            }
        }
        let other = new Sprite(ob.x, ob.y, "robot.png", oid);
        other.update = Sprite.prototype.update_travel;
        other.onleftclick = Sprite.prototype.onclick_set_destination;
        other.onrightclick = Sprite.prototype.onclick_throw_fireball;
        other.name = ob.name;

        this.sprites.push(other);
    }

    fire(ob: any, oid: number): void {
        if (oid === id) {
            return;
        }
        for (const sprite of this.sprites) {
            if (sprite.speed === 300) {
                continue;
            }
            if (oid === sprite.id && !(ob.who in this.used)) {
                sprite.onrightclick(ob.x, ob.y);
                this.used[ob.who] = true;
                return;
            }
        }
    }

    checkcollide(fireball: Sprite): void {
        let first = fireball.gethitbox();
        for (const sprite of this.sprites) {
            if (sprite === this.avatar) {
                console.log('hey');
                continue;
            }
            if (sprite.speed === 300) continue;
            let second = sprite.gethitbox();
            let collide = true;
            if (first.r < second.l) collide = false;
            if (first.l > second.r) collide = false;
            if (first.t > second.b) collide = false;
            if (first.b < second.t) collide = false;
            if (collide) {
                fireball.arrive();
                game?.controller.collisionHandler(sprite);
                console.log(sprite);
                return;
            }
        }
    }

    onleftclick(x: number, y: number): void {
        this.avatar.onleftclick(x, y);
    }

    onrightclick(x: number, y: number): void {
        this.avatar.onrightclick(x, y);
    }
}

class View {
    model: Model;
    canvas: HTMLCanvasElement;
    scrollX: number;
    scrollY: number;

    constructor(model: Model) {
        this.model = model;
        this.canvas = document.getElementById("myCanvas") as HTMLCanvasElement;
        this.scrollX = 0;
        this.scrollY = 0;
    }

    update(): void {
        // Clear the screen
        let ctx = this.canvas.getContext("2d")!;
        ctx.clearRect(0, 0, 1000, 500);

        // Sort the sprites by their y-value to create a pseudo-3D effect
        this.model.sprites.sort((a, b) => a.y - b.y);

        // set scroll values to follow avatar
        this.scrollX = this.model.avatar.x - 500;
        this.scrollY = this.model.avatar.y - 250;

        // Draw all the sprites
        for (const sprite of this.model.sprites) {
            let x = sprite.x - this.scrollX;
            let y = sprite.y - this.scrollY;
            ctx.drawImage(sprite.image, x - sprite.image.width / 2, y - sprite.image.height);
            if (sprite.hasOwnProperty("name")) {
                ctx.font = "24px serif";
                ctx.fillText(sprite.name!, x - sprite.image.width / 2, y - sprite.image.height);
            }
        }
    }
}

class Controller {
    model: Model;
    view: View;

    constructor(model: Model, view: View) {
        this.model = model;
        this.view = view;
        let self = this;

        // Add event listeners
        view.canvas.addEventListener("click", function (event) { self.onLeftClick(event); return false; });
        view.canvas.addEventListener("contextmenu", function (event) { self.onRightClick(event); return false; });
    }

    async collisionHandler(sprite: Sprite): Promise<void> {
        let payload = {
            id: id,
            mapitem: false,
        };
        // Check if it is a mapitem, if it isn't it must be a player
        if (sprite.id === id) {
            payload['mapitem'] = true;
            // Remove mapitem
            for (let i = 0; i < game.model.sprites.length; i++) {
                if (game?.model.sprites[i] === sprite) {
                    game?.model.sprites.splice(i, 1); // remove this sprite from the list
                }
            }
        }
        let response = await fetch('collision', {
            body: JSON.stringify(payload),
            cache: "no-cache",
            headers: {
                'Content-Type': 'application/json',
            },
            method: "POST",
        });
        const text = await response.text();
        console.log(`The server replied ${text}`);
    }

    async onLeftClick(event: MouseEvent): Promise<void> {
        event.preventDefault();
        const x = event.pageX - this.view.canvas.offsetLeft + this.view.scrollX;
        const y = event.pageY - this.view.canvas.offsetTop + this.view.scrollY;
        console.log(this.view.scrollX);
        console.log(this.view.scrollY);
        this.model.onleftclick(x, y);

        // Tell the server about this click
        let payload = {
            id: id,
            x: x,
            y: y,
        };
        let response = await fetch('leftClick', {
            body: JSON.stringify(payload),
            cache: "no-cache",
            headers: {
                'Content-Type': 'application/json',
            },
            method: "POST",
        });
        const text = await response.text();
        console.log(`The server replied ${text}`);
    }

    async onRightClick(event: MouseEvent): Promise<void> {
        event.preventDefault(); // Suppress the context menu
        const x = event.pageX - this.view.canvas.offsetLeft + this.view.scrollX;
        const y = event.pageY - this.view.canvas.offsetTop + this.view.scrollY;
        this.model.onrightclick(x, y);

        // Tell the server about this click
        let payload = {
            id: id,
            x: x,
            y: y,
        };
        let response = await fetch('rightClick', {
            body: JSON.stringify(payload),
            cache: "no-cache",
            headers: {
                'Content-Type': 'application/json',
            },
            method: "POST",
        });
        const text = await response.text();
        console.log(`The server replied ${text}`);
    }

    async update(): Promise<void> {
        // Ensure we do not hammer the server with too many update requests
        let now = new Date();
        if (now.getTime() - last_update_time.getTime() > 500) { // milliseconds
            last_update_time = now;
            // Collect all players from server
            let payload = {
                id: id,
            };
            let response = await fetch('update', {
                body: JSON.stringify(payload),
                cache: "no-cache",
                headers: {
                    'Content-Type': 'application/json',
                },
                method: "POST",
            });
            const text = await response.text();
            console.log(`The server replied ${text}`);
            let response_ob = JSON.parse(text);
            let fireballs = response_ob.fireballs;
            let players = response_ob.players;
            let scores: [string, number][] = [];
            for (let key in players) {
                this.model.add(players[key], parseInt(key));
                // Create array to sort scores
                scores.push([players[key].name, players[key].score]);
                scores.sort((a, b) => a[1] - b[1]);
                scores.reverse();
                console.log(scores);
            }
            for (let key in fireballs) {
                this.model.fire(fireballs[key], parseInt(key));
            }
            let leaderboard = document.getElementById('leaderboard')!;
            leaderboard.innerHTML = '';
            for (let score of scores) {
                console.log(score[1]);
                const newParagraph = document.createElement("p");
                newParagraph.innerHTML = `${score[0]}:${score[1]}`;
                if (score[0] === person) newParagraph.innerHTML = `<mark>${score[0]}:${score[1]}</mark>`;
                leaderboard.append(newParagraph);
            }
            console.log(scores);
        }
    }
}

class Game {
    model: Model;
    view: View;
    controller: Controller;

    constructor() {
        this.model = new Model();
        this.view = new View(this.model);
        this.controller = new Controller(this.model, this.view);
    }

    onTimer(): void {
        this.controller.update();
        this.model.update();
        this.view.update();
    }
}

let game: Game = new Game();
let timer = setInterval(() => { game.onTimer(); }, 30);

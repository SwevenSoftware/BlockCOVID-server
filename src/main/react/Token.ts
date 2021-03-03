class Token {

    public get() : string | null {
        return localStorage.getItem("TOKEN_AUTH");
    }

    public set(token : string) : void {
        localStorage.setItem("TOKEN_AUTH", token);
    }

    public remove() : void {
        localStorage.removeItem("TOKEN_AUTH");
    }
}

export default Token;
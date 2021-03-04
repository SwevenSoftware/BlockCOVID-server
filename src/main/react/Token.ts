class Token {

    public static get() : string | null {
        return localStorage.getItem("TOKEN_AUTH");
    }

    public static set(token : string) : void {
        localStorage.setItem("TOKEN_AUTH", token);
    }

    public static remove() : void {
        localStorage.removeItem("TOKEN_AUTH");
    }
}

export default Token;
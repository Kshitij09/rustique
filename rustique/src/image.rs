pub struct Bgra {
    b: u8,
    g: u8,
    r: u8,
    a: u8,
}

impl Bgra {
    pub fn grayscale(self: &mut Self) {
        let gray = ((0.299 * self.r as f32) +
            (0.587 * self.g as f32) +
            (0.114 * self.b as f32)) as u8;
        self.r = gray;
        self.g = gray;
        self.b = gray;
    }

    pub fn invert(self: &mut Self) {
        self.r = 255 - self.r;
        self.g = 255 - self.g;
        self.b = 255 - self.b;
    }
}
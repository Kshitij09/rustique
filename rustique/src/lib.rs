pub fn add(left: u64, right: u64) -> u64 {
    left + right
}
#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {
    use jni::objects::JClass;
    use jni::JNIEnv;
    use crate::add;

    // The native function implemented in Rust.
    #[unsafe(no_mangle)]
    pub unsafe extern "C" fn Java_com_kshitijpatil_rustique_Rustique_add(
        _: JNIEnv,
        _: JClass,
        left: u64,
        right: u64
    ) -> u64 {
        add(left, right)
    }
}
#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}

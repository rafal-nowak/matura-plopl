import {
    Box,
    Button,
    Container,
    DarkMode,
    Flex,
    Heading,
    Image,
    Text,
    VStack,
    Stack,
    Divider
} from "@chakra-ui/react";
import { Link } from "react-router-dom";
import { FaCheckCircle } from "react-icons/fa";
import { FaLaptopCode, FaRocket } from "react-icons/fa6";
import { motion } from "framer-motion";

export const Home = () => {
    const logoPath = "/logo.png";
    const darkBlue = "#0B132B";
    const accentColor = "#FFD700";
    const darkSlateBlue = "#1C2541";
    const lightCharcoal = "#3A506B";

    const fadeIn = {
        hidden: { opacity: 0, y: 20 },
        visible: { opacity: 1, y: 0, transition: { duration: 0.8 } }
    };

    return (
        <DarkMode>
            <Box bg={darkBlue} color="white">
                <Flex as="nav" bg={darkBlue} p={6} justify="space-between" align="center">
                    <Image src={logoPath} alt="CheckIT Logo" boxSize="60px" />
                    <Flex>
                        <Button as={Link} to="/login" colorScheme="yellow" variant="solid" mx={2}>
                            Zaloguj się
                        </Button>
                        <Button as={Link} to="/register" colorScheme="yellow" mx={2}>
                            Zarejestruj się
                        </Button>
                    </Flex>
                </Flex>

                <Box py={28} textAlign="center" bg={darkSlateBlue} as={motion.div} initial="hidden" animate="visible" variants={fadeIn}>
                    <Container maxW="6xl">
                        <Heading as="h1" size={["3xl", "4xl"]} mb={4}>
                            Sprawdź swoje umiejętności z CheckIT!
                        </Heading>
                        <Text fontSize="xl" mb={8}>
                            Przygotuj się szybko i skutecznie do matury z informatyki!
                        </Text>
                        <Button colorScheme="yellow" size="lg" as={Link} to="/register" _hover={{ bg: accentColor }}>
                            Rozpocznij naukę
                        </Button>
                    </Container>
                </Box>

                <Box py={20} bg={lightCharcoal}>
                    <Container maxW="6xl">
                        <Heading as="h2" size="2xl" textAlign="center" mb={12}>
                            Dlaczego warto wybrać CheckIT?
                        </Heading>
                        <Stack direction={{ base: "column", md: "row" }} spacing={8} justify="center">
                            {[{
                                icon: FaLaptopCode,
                                title: "Intuicyjny edytor kodu",
                                description: "Pisanie kodu z autouzupełnianiem w Pythonie i nie tylko. Stwórz rozwiązanie bez trudu."
                            }, {
                                icon: FaCheckCircle,
                                title: "Błyskawiczna Weryfikacja",
                                description: "Automatyczne sprawdzanie zadań pozwala szybko ocenić poprawność Twoich rozwiązań."
                            }, {
                                icon: FaRocket,
                                title: "Przygotowanie do Matury",
                                description: "Rozwiązuj zadania maturalne i zdobądź pewność siebie przed egzaminem."
                            }].map(({ icon: Icon, title, description }) => (
                                <VStack key={title} align="center" spacing={6} maxW="400px">
                                    <Box
                                        bg={darkSlateBlue}
                                        p={8}
                                        borderRadius="2xl"
                                        boxShadow="2xl"
                                        width="100%"
                                        as={motion.div}
                                        whileHover={{ scale: 1.05 }}
                                        transition={{ duration: 0.3 }}
                                    >
                                        <Icon size="40px" color={accentColor} />
                                        <Heading as="h3" size="lg" my={4}>{title}</Heading>
                                        <Text>{description}</Text>
                                    </Box>
                                </VStack>
                            ))}
                        </Stack>
                    </Container>
                </Box>

                <Box py={20} bg={darkSlateBlue}>
                    <Container maxW="6xl">
                        <Heading as="h2" size="2xl" textAlign="center" mb={8}>
                            Nasi Partnerzy
                        </Heading>
                        <Flex justify="center" wrap="wrap" gap={6}>
                            <Box maxW="200px" p={4} textAlign="center">
                                <Image src="/ZzT_logo.png" alt="Zwolnieni z Teorii Logo" maxW="100%" />
                            </Box>
                        </Flex>
                    </Container>
                </Box>

                <Divider />
                <Box bg={darkBlue} color="white" py={6} textAlign="center">
                    <Text color={lightCharcoal}>&copy; 2025 CheckIT - Wszystkie prawa zastrzeżone.</Text>
                </Box>
            </Box>
        </DarkMode>
    );
};
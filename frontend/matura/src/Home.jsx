import {
    Box,
    Button,
    Container,
    DarkMode,
    Flex,
    Heading,
    Image,
    Text,
    VStack
} from "@chakra-ui/react";
import { Link } from "react-router-dom";

export const Home = () => {
    const logoPath = "logo.png";

    const darkBlue = "#121C2B";
    const accentColor = "#C8A500";
    const darkSlateBlue = "#2E3A47";
    const lightCharcoal = "#5A5C61";

    return (
        <DarkMode>
            <Box bg={darkBlue} color="white">
                <Flex as="nav" bg={darkBlue} color="white" p={6} justify="space-between" align="center">
                    <Image src={logoPath} alt="CheckIT Logo" boxSize="50px" />
                    <Flex>
                        <Button as={Link} to="/login" colorScheme="yellow" variant="outline" mx={3} _hover={{ bg: accentColor }}>
                            Zaloguj się
                        </Button>
                        <Button as={Link} to="/register" colorScheme="yellow" mx={3} _hover={{ bg: accentColor }}>
                            Zarejestruj się
                        </Button>
                    </Flex>
                </Flex>

                <Box py={24} textAlign="center" bg={darkSlateBlue}>
                    <Container maxW="6xl">
                        <Heading as="h1" size="3xl" mb={4} color="white">
                            Sprawdź swoje umiejętności z CheckIT!
                        </Heading>
                        <Text fontSize="xl" mb={8} color={lightCharcoal}>
                            Narzędzie do automatycznego sprawdzania zadań maturalnych z informatyki. Pomagamy Ci
                            przygotować się na maturę!
                        </Text>
                        <Button colorScheme="yellow" size="lg" as={Link} to="/register" _hover={{ bg: accentColor }}>
                            Rozpocznij naukę
                        </Button>
                    </Container>
                </Box>

                <Box py={16} bg={lightCharcoal}>
                    <Container maxW="6xl">
                        <Heading as="h2" size="2xl" textAlign="center" mb={12}>
                            Co oferuje CheckIT?
                        </Heading>
                        <Flex
                            wrap="wrap"
                            justify="center"
                            gap={6}
                            mx={{ base: 4, md: 0 }}
                        >
                            <VStack align="center" spacing={6} maxW="300px" mb={{ base: 8, md: 0 }}>
                                <Box
                                    bg={darkBlue}
                                    p={8}
                                    borderRadius="md"
                                    boxShadow="lg"
                                    width="100%"
                                    mb={6}
                                >
                                    <Heading as="h3" size="lg" color="white" mb={4}>
                                        Edytor kodu w czasie rzeczywistym
                                    </Heading>
                                    <Text color="white" textAlign="center">
                                        Pisanie kodu z funkcją autouzupełniania.
                                        Szybkie i łatwe przygotowanie rozwiązania.
                                    </Text>
                                </Box>
                            </VStack>
                            <VStack align="center" spacing={6} maxW="300px" mb={{ base: 8, md: 0 }}>
                                <Box
                                    bg={darkBlue}
                                    p={8}
                                    borderRadius="md"
                                    boxShadow="lg"
                                    width="100%"
                                    mb={6}
                                >
                                    <Heading as="h3" size="lg" color="white" mb={4}>
                                        Automatyczne sprawdzanie zadań
                                    </Heading>
                                    <Text color="white" textAlign="center">
                                        Nasz system automatycznie sprawdza zadania na podstawie wyników Twojego
                                        programu. Szybka i precyzyjna ocena!
                                    </Text>
                                </Box>
                            </VStack>
                            <VStack align="center" spacing={6} maxW="300px" mb={{ base: 8, md: 0 }}>
                                <Box
                                    bg={darkBlue}
                                    p={8}
                                    borderRadius="md"
                                    boxShadow="lg"
                                    width="100%"
                                    mb={6}
                                >
                                    <Heading as="h3" size="lg" color="white" mb={4}>
                                        Zadania maturalne
                                    </Heading>
                                    <Text color="white" textAlign="center">
                                        Wybierz zadanie z maturalnych arkuszy, rozwiąż je i sprawdzaj swoje
                                        umiejętności w każdej chwili!
                                    </Text>
                                </Box>
                            </VStack>
                        </Flex>
                    </Container>
                </Box>


                <Box py={24} bg={darkSlateBlue}>
                    <Container maxW="6xl">
                        <Heading as="h2" size="2xl" textAlign="center" mb={8}>
                            Nasi Partnerzy
                        </Heading>
                        <Flex justify="center" wrap="wrap" gap={8}>
                            <Box maxW="200px" p={4} textAlign="center">
                                <Image src="zwolnieni-z-teorii-logo.png" alt="Logo Zwolnionych z Teorii"
                                       maxW="100%" />
                            </Box>
                        </Flex>
                    </Container>
                </Box>

                <Box bg={darkBlue} color="white" py={6} textAlign="center">
                    <Text color={lightCharcoal}>&copy; 2025 CheckIT - Wszystkie prawa zastrzeżone.</Text>
                </Box>
            </Box>
        </DarkMode>
    );
};
